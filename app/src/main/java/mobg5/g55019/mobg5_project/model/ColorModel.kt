package mobg5.g55019.mobg5_project.model

/**
 * Enumeration representing the colors that a beer can have.
 *
 * Each enum value has a hex code representing the color.
 */
enum class ColorModel(val hex: String) {
    BLONDE("#dbc7a0"),
    BRUNE("#7d3a15"),
    ROUSSE("#ad4f09"),
    AMBREE("#FFBF00"),
    DOREE("#FFD700"),
    CUIVREE("#B87333"),
    NOIRE("#000000"),
    VERTE("#00FF00"),
    BLANCHE("#FFFFFF"),
    ROUGE("#EB4034"),
    ORANGE("#FFA500"),
    JAUNE("#FFFF00"),
    VIOLETTE("#EE82EE"),
    DEFAULT("#000000")
}