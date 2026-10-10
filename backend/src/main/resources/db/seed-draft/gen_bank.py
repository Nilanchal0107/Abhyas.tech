"""Builds bank_starter.csv, then per wave (ticket 16) review.md + V8__starter_bank.sql and review_wave2.md + V9__starter_bank_wave2.sql (FR-20, skill-bank ticket 06) from the bank_*.py data files, and checks them.

Run from this folder:  python gen_bank.py
Covers every skill of the 11 starter roles (owner, 4 October 2026). Each skill has 10 multiple-correct and 10 fill-in-the-blank
questions (3 easy, 4 medium, 3 hard) and 5 brief questions with 3 key facts each (the brief checklist grades key_fact_1 to
key_fact_3). Soft skills (SOFT) have only the 5 briefs. Plus behavioral questions by theme, for the brief and voice parts.
The questions are general knowledge written by Claude and have NOT been reviewed. Review review.md, fix the questions in the bank_*.py
file named in its heading, run again, then move that wave's SQL file to ../migration/. Flyway only runs files in that folder.
A row loaded with source 'seed' counts as reviewed (skill-bank D3). The earlier draft for the 7 old software roles is in git history.
"""
import csv, hashlib, json, re, sys
from pathlib import Path
from bank_common import BLANK
import bank_fullstack, bank_software, bank_frontend, bank_data, bank_dataeng, bank_devops

HERE = Path(__file__).parent
LEVELS = ["easy", "medium", "hard"]
THEMES = ["teamwork", "conflict", "failure", "leadership", "deadline_pressure"]
FILES = [bank_fullstack, bank_software, bank_frontend, bank_data, bank_dataeng, bank_devops]
# Wave 1 = skills of these roles (ticket 16, D12); wave 2 = every other skill. A skill of both waves is loaded once, in wave 1.
WAVE1_ROLES = {"Software Engineer", "Backend Developer", "Frontend Developer", "Full Stack Developer", "AI Engineer", "Machine Learning Engineer"}
SOFT = {"Communication", "Critical Thinking"}  # scenario briefs only: they make poor multiple-correct or blank questions

FULL = {"multiple_correct": {"easy": 3, "medium": 4, "hard": 3}, "fill_blank": {"easy": 3, "medium": 4, "hard": 3}, "brief": 5}
BRIEFS_ONLY = {"multiple_correct": {"easy": 0, "medium": 0, "hard": 0}, "fill_blank": {"easy": 0, "medium": 0, "hard": 0}, "brief": 5}

Q, SOURCE = {}, {}  # skill -> questions, skill -> data file name
for _f in FILES:
    for _skill, _qs in _f.Q.items():
        assert _skill not in Q, f"{_skill} is in both {SOURCE[_skill]} and {_f.__name__}"
        Q[_skill], SOURCE[_skill] = _qs, _f.__name__ + ".py"
BEHAVIORAL = bank_fullstack.BEHAVIORAL


def hash_text(target, kind, text):
    """Same recipe as PracticeBank.learn: SHA-256 of skill-or-theme|type|text, stripped and lower-cased."""
    return hashlib.sha256(f"{target}|{kind}|{text}".strip().lower().encode("utf-8")).hexdigest()


def sql(s):
    return "NULL" if s is None else "'" + s.replace("'", "''") + "'"


def role_skills():
    """Skills of each starter role in database-schema.md section 12 (rows like "Category","Role","aliases","skills"): role -> skills."""
    text = (HERE.parents[5] / "database-schema.md").read_text(encoding="utf-8")
    return {row[1]: set(row[3].split("|")) for row in csv.reader(re.findall(r'^"[^"]+","[^"]+","[^"]*","[^"]+"$', text, re.M))}


def check(skill, q):
    """The rules Java applies to AI questions (SessionService.validGenerated, validBlanks), so a seed row never fails them."""
    kind, level, text = q[0], q[1], q[2]
    assert level in LEVELS, f"{skill}: bad level {level}"
    strings = [text]
    if kind == "multiple_correct":
        options, correct = q[3], q[4]
        assert len(options) == 4 and len(set(options)) == 4, f"{skill}: need 4 different options: {text}"
        assert correct and set(correct) <= set("ABCD") and len(set(correct)) == len(correct) < 4, f"{skill}: 1 to 3 correct letters: {text}"
        strings += options
    elif kind == "brief":
        facts, keywords = q[3], q[4]
        assert len(facts) == 3 and len(set(facts)) == 3 and all(len(f) <= 500 for f in facts), f"{skill}: a brief needs 3 different key facts: {text}"
        assert keywords, f"{skill}: a brief needs keywords: {text}"
        strings += facts + keywords
    else:
        accepted = q[3]
        blanks = text.count(BLANK)
        assert blanks in (1, 2) and blanks == len(accepted) and all(accepted), f"{skill}: blanks and accepted wordings differ: {text}"
        strings += [w for a in accepted for w in a]
    for s in strings:
        assert s.strip() and '"' not in s and "\\" not in s, f"{skill}: avoid empty strings, quotes and backslashes: {s}"


def main():
    by_role = role_skills()
    wanted = set().union(*by_role.values())
    wave1 = set().union(*(sk for role, sk in by_role.items() if role in WAVE1_ROLES))
    assert WAVE1_ROLES <= set(by_role), f"unknown wave 1 roles: {sorted(WAVE1_ROLES - set(by_role))}"
    wave_of = lambda skill: 1 if skill is None or skill in wave1 else 2  # behavioral rows (no skill) go in wave 1
    assert not set(Q) - wanted, f"not a skill of any starter role: {sorted(set(Q) - wanted)}"
    rows, hashes = [], set()  # (skill, theme, type, level, text, options, correct, accepted, key facts, keywords)
    for skill, qs in Q.items():
        want = BRIEFS_ONLY if skill in SOFT else FULL
        for kind in ("multiple_correct", "fill_blank"):
            got = {lv: sum(1 for q in qs if q[0] == kind and q[1] == lv) for lv in LEVELS}
            assert got == want[kind], f"{skill}: {kind} per level is {got}, want {want[kind]}"
        assert sum(1 for q in qs if q[0] == "brief") == want["brief"], f"{skill}: need {want['brief']} brief questions"
        for q in qs:
            check(skill, q)
            kind = q[0]
            options = json.dumps([{"key": k, "text": t} for k, t in zip("ABCD", q[3])], ensure_ascii=False) if kind == "multiple_correct" else None
            correct = json.dumps(list(q[4])) if kind == "multiple_correct" else None
            accepted = json.dumps(q[3], ensure_ascii=False) if kind == "fill_blank" else None
            facts = json.dumps(q[3], ensure_ascii=False) if kind == "brief" else None
            keywords = json.dumps(q[4], ensure_ascii=False) if kind == "brief" else None
            rows.append((skill, None, kind, q[1], q[2], options, correct, accepted, facts, keywords))
    for kind, theme, text in BEHAVIORAL:
        assert theme in THEMES and kind in ("brief", "voice") and '"' not in text, f"bad behavioral row: {text}"
        rows.append((None, theme, kind, None, text, None, None, None, None, None))
    for skill, theme, kind, _, text, *_ in rows:
        h = hash_text(skill or theme, kind, text)
        assert h not in hashes, f"duplicate question {text}"
        hashes.add(h)
    if wanted - set(Q):  # everything present passed the checks; files are written only when every skill is there
        sys.exit(f"checks passed for {len(Q)} skills; still missing {len(wanted - set(Q))}: {sorted(wanted - set(Q))}")

    with open(HERE / "bank_starter.csv", "w", encoding="utf-8", newline="") as f:
        w = csv.writer(f, quoting=csv.QUOTE_ALL)
        w.writerow(["skill", "theme", "type", "difficulty", "text", "options", "correct_options", "accepted_answers", "key_facts", "keywords"])
        w.writerows([[c if c is not None else "" for c in r] for r in rows])

    order = {"multiple_correct": 0, "fill_blank": 1, "brief": 2}
    for wave, (review_name, sql_name, version) in {1: ("review.md", "V8__starter_bank.sql", "V8"), 2: ("review_wave2.md", "V9__starter_bank_wave2.sql", "V9")}.items():
        md = [f"# Practice bank review, wave {wave}: " + ("skills of the Software Development and AI roles" if wave == 1 else "skills of the Data, Cloud and DevOps roles not in wave 1"), "",
              "Check every answer. Correct options are **bold**. Fix a question in the `bank_*.py` file named in its heading, run `python gen_bank.py`",
              f"again, then move `{sql_name}` to `db/migration/`.", ""]
        for skill, qs in Q.items():
            if wave_of(skill) != wave:
                continue
            md += [f"## {skill} ({SOURCE[skill]})", ""]
            for q in sorted(qs, key=lambda q: (order[q[0]], LEVELS.index(q[1]))):
                if q[0] == "multiple_correct":
                    md.append(f"- [ ] **MC, {q[1]}:** {q[2]}")
                    md += [f"  - {k}. " + (f"**{t}**" if k in q[4] else t) for k, t in zip("ABCD", q[3])]
                elif q[0] == "brief":
                    md.append(f"- [ ] **Brief, {q[1]}:** {q[2]}")
                    md += [f"  - Key fact: {f}" for f in q[3]]
                    md.append("  - Keywords (grading without AI): " + ", ".join(q[4]))
                else:
                    md.append(f"- [ ] **Blank, {q[1]}:** {q[2]}  ")
                    md.append("  Accepted: " + " / ".join(", ".join(a) for a in q[3]))
            md.append("")
        if wave == 1:
            md += ["## Behavioral", ""] + [f"- [ ] **{kind}, {theme}:** {text}" for kind, theme, text in BEHAVIORAL]
        (HERE / review_name).write_text("\n".join(md) + "\n", encoding="utf-8")

        lines = [f"-- {version}: starter practice bank, wave {wave} (FR-20, skill-bank tickets 06 and 16)" + (", plus behavioral questions." if wave == 1 else "."),
                 "-- Generated by gen_bank.py. General knowledge, reviewed by the owner before this file was moved to db/migration/.",
                 "-- Rows are shared by all users, marked 'seed' (reviewed), and hold no personal data.", ""]
        cols = "INSERT INTO bank_question (type, skill_id, theme, difficulty, text_template, options, correct_options, accepted_answers, key_facts, keywords, source, text_hash)"
        for skill, theme, kind, level, text, options, correct, accepted, facts, keywords in rows:
            if wave_of(skill) != wave:
                continue
            values = lambda skill_id: (f"{sql(kind)}, {skill_id}, {sql(theme)}, {sql(level)}, {sql(text)}, {sql(options)}, {sql(correct)}, {sql(accepted)}, "
                                       f"{sql(facts)}, {sql(keywords)}, 'seed', {sql(hash_text(skill or theme, kind, text))}")
            if skill:
                lines.append(f"{cols}\nSELECT {values('id')}\nFROM skill WHERE name = {sql(skill)};")
            else:
                lines.append(f"{cols}\nVALUES ({values('NULL')});")
        (HERE / sql_name).write_text("\n".join(lines) + "\n", encoding="utf-8")
    from collections import Counter
    for wave in (1, 2):
        print(f"wave {wave}:", dict(Counter(r[2] for r in rows if r[0] and wave_of(r[0]) == wave)), f"for {sum(1 for k in Q if wave_of(k) == wave)} skills")
    print(f"{len(BEHAVIORAL)} behavioral (wave 1)")


if __name__ == "__main__":
    sys.exit(main())
