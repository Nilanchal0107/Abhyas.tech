package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.abhyas.trainer.session.Grading.Tick;

/**
 * Grades a brief or voice answer without AI (FR-20, trd 7.5): ticks each checklist item by keyword match against the key facts and
 * cue-word patterns, with the matching sentence of the answer as the quote. Every quote is a sentence cut from the answer itself,
 * so the evidence rule always holds. It is a rough grader on purpose: its marks never count toward mastery.
 */
final class KeywordGrader {
    private KeywordGrader() {}

    private static final Set<String> STOP = Set.of("the", "and", "that", "this", "with", "for", "from", "have", "has", "had", "was", "were", "are", "you", "your", "our", "their",
            "what", "when", "which", "how", "why", "did", "does", "about", "into", "using", "used", "use", "also", "then", "than", "them", "they", "will", "would", "could",
            "should", "can", "not", "but", "its", "his", "her", "who", "any", "all", "out", "one", "two", "over", "more", "most", "some", "such", "each", "very");
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}][\\p{L}\\p{N}+#.]*");
    private static final Pattern SENTENCE_END = Pattern.compile("(?<=[.!?])\\s+|\\R+");
    private static final Pattern FIRST_PERSON = Pattern.compile("(?<!\\p{L})I(?!\\p{L})");
    private static final Pattern WE = Pattern.compile("(?<!\\p{L})(we|our|us)(?!\\p{L})", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHY = cue("because|since|so that|in order to|due to|therefore|that is why|which is why|as a result|the reason|to avoid|to reduce|to ensure|to prevent");
    private static final Pattern EXAMPLE = Pattern.compile("(?i)(\\b(for example|for instance|such as|in my|in our|when i|i used|we used)\\b|e\\.g\\.|\\d)");
    private static final Pattern SITUATION = cue("when|while|at|during|was working|were working|the project|the team|situation|context|problem|challenge|needed|had to|responsible|assigned");
    private static final Pattern ONLY_SITUATION = cue("when|while|at|during|was working|were working|the project|the team|situation|context|problem|challenge");
    private static final Pattern TASK = cue("needed to|had to|task|goal|responsible for|my job|was asked|assigned|objective|needed");
    private static final Pattern ACTION = Pattern.compile("(?<!\\p{L})I (?i:(?:\\w+ed|built|wrote|led|made|chose|ran|set up|took|decided|created|fixed|implemented|designed|added|started|organi[sz]ed|talked|spoke|asked|suggested|proposed|split|moved|replaced|rewrote))\\b");
    private static final Pattern RESULT = Pattern.compile("(?i)(\\b(result(ed|s)?|reduced|increased|improved|decreased|saved|delivered|launched|shipped|achieved|outcome|ended up|finally)\\b|\\d\\s?%)");
    private static final Pattern LESSON = cue("learn(ed|t)|lesson|realized|realised|next time|in future|in the future|would do differently|takeaway|taught me");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    // design areas (ticket 09): rough cue words only, so a keyword-graded design answer never counts toward mastery
    private static final Pattern REQUIREMENTS = cue("requirements?|assum(e|es|ing|ptions?)|users?|traffic|per second|qps|functional|non-functional|constraints?|scope|clarify|expected");
    private static final Pattern API = cue("endpoints?|api|get|post|put|delete|request|response|method|methods|interface|class|classes|function|operations?");
    private static final Pattern DATA_MODEL = cue("tables?|schema|columns?|entit(y|ies)|database|index(es)?|primary key|foreign key|data model|fields?|relationships?|objects?|store");
    private static final Pattern SCALE = cue("scal(e|es|ing|able)|cach(e|es|ing)|shard(s|ing)?|partition(s|ing)?|replicas?|replication|load balanc\\w*|queues?|throughput|latency|horizontal(ly)?|bottleneck|concurren\\w*");
    private static final Pattern TRADEOFFS = cue("trade-?offs?|alternatives?|instead|downsides?|versus|vs|pros and cons|consistency|availability|cost|however|at the cost");

    private static Pattern cue(String alternatives) { return Pattern.compile("(?i)\\b(" + alternatives + ")\\b"); }

    /** Splits an answer into sentences cut from its own text. */
    static List<String> sentences(String answer) {
        List<String> out = new ArrayList<>();
        for (String s : SENTENCE_END.split(answer == null ? "" : answer)) if (!s.isBlank()) out.add(s.strip());
        return out;
    }

    /** Lowercase words of 3+ characters that are not filler words. */
    static Set<String> significant(String text) {
        Set<String> out = new HashSet<>();
        Matcher m = WORD.matcher(text == null ? "" : text.toLowerCase(Locale.ROOT));
        while (m.find()) {
            String w = m.group().replaceAll("[.]+$", "");
            if (w.length() >= 3 && !STOP.contains(w)) out.add(w);
        }
        return out;
    }

    /** The first sentence that contains at least {@code need} of the words, or null. */
    private static String sentenceWith(List<String> sentences, Set<String> words, int need) {
        if (words.isEmpty()) return null;
        for (String s : sentences) {
            Set<String> have = significant(s);
            if (words.stream().filter(have::contains).count() >= Math.min(need, words.size())) return s;
        }
        return null;
    }

    private static String sentenceMatching(List<String> sentences, Pattern p) {
        for (String s : sentences) if (p.matcher(s).find()) return s;
        return null;
    }

    /**
     * @param keys      the checklist items of this attempt ({@link Grading#itemKeys})
     * @param facts     the key facts of the question's resume point (empty when none were drafted)
     * @param keywords  extra words from the practice bank row, may be empty
     * @param pointText the resume point the question is about, or the question text when it has none
     */
    static List<Tick> grade(String answer, List<String> keys, List<String> facts, List<String> keywords, String pointText, String questionText) {
        List<String> sentences = sentences(answer);
        Set<String> topic = significant(pointText + " " + String.join(" ", keywords));
        List<Tick> out = new ArrayList<>();
        for (String key : keys) {
            String quote = null;
            if (key.startsWith("key_fact_")) {
                int i = Integer.parseInt(key.substring("key_fact_".length())) - 1;
                Set<String> words = i < facts.size() ? significant(facts.get(i)) : topic;
                quote = sentenceWith(sentences, words, Math.max(1, (words.size() + 1) / 2));
            } else switch (key) {
                case "explains_why" -> quote = sentenceMatching(sentences, WHY);
                case "consistent_with_resume" -> quote = sentenceWith(sentences, topic.isEmpty() ? significant(questionText) : topic, 1);
                case "concrete_example" -> quote = sentenceMatching(sentences, EXAMPLE);
                case "own_role" -> quote = ownRole(answer, sentences);
                case "situation_task" -> quote = sentenceMatching(sentences, SITUATION);
                case "situation" -> quote = sentenceMatching(sentences, ONLY_SITUATION);
                case "task" -> quote = sentenceMatching(sentences, TASK);
                case "action" -> quote = sentenceMatching(sentences, ACTION);
                case "result" -> quote = sentenceMatching(sentences, RESULT);
                case "lesson_learned" -> quote = sentenceMatching(sentences, LESSON);
                case "concrete_detail" -> quote = sentenceMatching(sentences, DIGIT);
                case "requirements" -> quote = sentenceMatching(sentences, REQUIREMENTS);
                case "api" -> quote = sentenceMatching(sentences, API);
                case "data_model" -> quote = sentenceMatching(sentences, DATA_MODEL);
                case "scale" -> quote = sentenceMatching(sentences, SCALE);
                case "tradeoffs" -> quote = sentenceMatching(sentences, TRADEOFFS);
                case "answers_question" -> {
                    Set<String> q = significant(questionText);
                    quote = sentenceWith(sentences, q, q.size() >= 4 ? 2 : 1);
                }
                case "clear_and_focused" -> {
                    VoiceStats v = VoiceStats.of(answer, 0);
                    if (v.wordCount() >= 30 && v.fillerCount() * 20 <= v.wordCount() && !sentences.isEmpty()) quote = sentences.get(0);
                }
                default -> { }
            }
            out.add(new Tick(key, Grading.label(key, facts), quote != null, quote));
        }
        return out;
    }

    /** "I" with an action at least as often as "we" appears, and at least once ("I don't know" is not a role). */
    private static String ownRole(String answer, List<String> sentences) {
        long me = FIRST_PERSON.matcher(answer).results().count(), we = WE.matcher(answer).results().count();
        return me < we ? null : sentenceMatching(sentences, ACTION);
    }
}
