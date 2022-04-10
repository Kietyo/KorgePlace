import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.localVfs
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class PlaceDataIterator(
    val placeDataFolderPath: String
) : Iterator<PlaceEntryWithTimestampDelta> {
    val resource = localVfs(placeDataFolderPath)
    val files = runBlockingNoJs { resource.listSimple() }.iterator()
    var currentEntriesIterator = readNextFileIterator()

    init {
        println(files)
    }

    fun readNextFileIterator(): MutableIterator<PlaceEntryWithTimestampDelta>? {
        if (!files.hasNext()) return null
        val nextFile = files.next()
        println("Reading next file: $nextFile")
        return runBlockingNoJs { ProtoBuf
            .decodeFromByteArray<MutableList<PlaceEntryWithTimestampDelta>>(
                nextFile.readBytes()) }.iterator()
    }

    override fun hasNext(): Boolean {
        if (currentEntriesIterator?.hasNext() == true) {
            return true
        }

        currentEntriesIterator = readNextFileIterator()

        return currentEntriesIterator?.hasNext() == true
    }

    override fun next(): PlaceEntryWithTimestampDelta {
        if (hasNext()) {
            return currentEntriesIterator!!.next()
        }
        TODO("Error")
    }

}