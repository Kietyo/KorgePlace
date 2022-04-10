import com.soywiz.klock.TimeSpan
import com.soywiz.korge.*
import com.soywiz.korge.input.draggable
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.clicked
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korgw.GameWindow
import com.soywiz.korim.bitmap.Bitmap16
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.substr
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

fun String.convertToColor(): RGBA {
    val str = this.lowercase()
    require(str.startsWith("#"))
    val hex = str.substr(1)
    if (hex.length !in setOf(3, 4, 6, 8)) return Colors.BLACK
    val chars = if (hex.length < 6) 1 else 2
    val scale = if (hex.length < 6) (255.0 / 15.0) else 1.0
    val r = (hex.substr(0 * chars, chars).toInt(0x10) * scale).toInt()
    val g = (hex.substr(1 * chars, chars).toInt(0x10) * scale).toInt()
    val b = (hex.substr(2 * chars, chars).toInt(0x10) * scale).toInt()
    return RGBA(r, g, b)
}

suspend fun main() = Korge(
    width = 1000, height = 1000, bgcolor = Colors["#2b2b2b"],
    quality = GameWindow.Quality.QUALITY,
    clipBorders = false
) {


    val metadataFile = resourcesVfs["metadata.json"]
    val metadata = Json.decodeFromString<Metadata>(metadataFile.readString())
    val idToColorMap = metadata.colorToId.entries.associateBy(
        { it.value.toInt() }, { Colors[it.key] }
    )

    println(metadataFile)
    println(metadata)
    println(idToColorMap)

    val colorsArray = Array<RGBA>(32) {
        Colors.WHITE
    }
    repeat(32) {
        colorsArray[it] = idToColorMap[it]!!
    }

    println(colorsArray.toList())

    val entriesIterator = PlaceDataIterator("C:\\Users\\kietm\\Downloads\\place_data_compressed2")

    //	println(entries)


//    val entriesIterator = entries.iterator()


    var bitmap = Bitmap32(2000, 2000, Colors.WHITE)

    val imageContainer = container {
        image(bitmap)
        draggable()
    }
    val inputProcessor = CameraInputProcessor(imageContainer, maxZoomIn = 7.0)
    addComponent(inputProcessor)
//    val button = uiButton("Update place!") {
//        onClick {
//            println("Clicked!")
//            val newBitmap = bitmap.clone()
//            repeat(10000) {
//                val entry = entriesIterator.next()
//                newBitmap[entry.coordinateX.toInt(), entry.coordinateY.toInt()] =
//                    colorsArray[entry.pixelColor.toInt()]
//            }
//            bitmap = newBitmap
//            imageContainer.removeChildren()
//            imageContainer.image(newBitmap)
//        }
//    }

    addFixedUpdater(TimeSpan(100.0)) {
        val newBitmap = bitmap.clone()
        repeat(100000) {
            val entry = entriesIterator.next()
            newBitmap.setRgba(entry.coordinateX.toInt(), entry.coordinateY.toInt(),
                colorsArray[entry.pixelColor.toInt()]
            )
        }
        bitmap = newBitmap
        imageContainer.removeChildren()
        imageContainer.image(newBitmap)
    }


    //	while (true) {
    //		image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
    //		image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
    //	}


}