"""Helpers the bank_*.py data files use to write questions. gen_bank.py checks and writes them."""
BLANK = "________"


def mc(level, text, options, correct):
    """Multiple-correct: 4 options, correct letters such as "AB" (1 to 3)."""
    return ("multiple_correct", level, text, options, correct)


def fb(level, text, accepted):
    """Fill-in-the-blank: one list of accepted wordings per BLANK in the text."""
    return ("fill_blank", level, text, accepted)


def br(level, text, facts, keywords):
    """A technical brief question: the 3 key facts a good answer covers, and keywords for grading without AI (KeywordGrader)."""
    return ("brief", level, text, facts, keywords)
