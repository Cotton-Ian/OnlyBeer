package mobg5.g55019.mobg5_project.model

data class Beer(
    val name: String,
    val brewery: String,
    val country: String,
    val shortDescription: String,
    val longDescription: String,
    val alcoholMin: Double,
    val alcoholMax: Double,
    val color: String,
    val image: Int,
    val type: String,
    val favourite: Boolean


)