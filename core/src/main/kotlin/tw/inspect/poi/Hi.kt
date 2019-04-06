package tw.inspect.poi

import io.reactivex.Flowable
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min


fun myMain(args: Array<String>) {

    (max(3, 2) until min(4, 7)).forEach {
        println(it)
    }
    val wb = createWorkbook( File("a.xlsx"))
    wb.cloneSheet(wb.getSheet("Sheet1"), "hi1" )
    P().copySheet2(wb, wb.getSheet("Sheet1"), "hi21" )
    P().copySheet2(wb, wb.getSheet("Sheet2"), "hi22" )
    Flowable.just("Hello world")


    val fileOut = FileOutputStream(incrementalName("b",".xlsx"))
    wb.write(fileOut)
    fileOut.flush()
    fileOut.close()

}

fun incrementalName(base : String, extension: String) : String{
    fun regulation(i : Int) : String {
        return if(i == 0) {
            ""
        } else {
            "($i)"
        }
    }
    fun construct(i: Int) = base + regulation(i) + extension
    var i = 0
    while (File(construct(i)).exists()) {
        i++
    }
    return construct(i)
}
