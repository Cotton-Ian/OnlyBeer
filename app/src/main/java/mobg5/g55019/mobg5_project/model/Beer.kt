package mobg5.g55019.mobg5_project.model

import java.io.Serializable

/**
 * Data class representing a beer.
 *
 * @property name the name of the beer
 * @property brewery the brewery that produces the beer
 * @property country the country where the beer is produced
 * @property shortDescription a short description of the beer
 * @property longDescription a long description of the beer
 * @property alcoholMin the minimum alcohol content of the beer
 * @property alcoholMax the maximum alcohol content of the beer
 * @property color the color of the beer
 * @property imageUrl the URL of an image of the beer
 * @property type the type of the beer
 */
data class Beer(
    val name: String,
    val brewery: String,
    val country: String,
    val shortDescription: String,
    val longDescription: String,
    val alcoholMin: Double,
    val alcoholMax: Double,
    val color: String,
    val imageUrl: String,
    val type: String,

) : Serializable