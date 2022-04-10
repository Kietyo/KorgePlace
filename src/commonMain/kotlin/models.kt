

@kotlinx.serialization.Serializable
data class PlaceEntryWithTimestampDelta(
    val timestampDelta: Int,
    val userId: Int,
    val pixelColor: UByte,
    val coordinateX: Short,
    val coordinateY: Short,
)

@kotlinx.serialization.Serializable
data class Metadata(
    val colorToId: Map<String, UByte>,
    val firstTimestamp: Long
)

