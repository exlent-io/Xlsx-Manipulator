package tw.inspect.poi

import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.*
import java.util.*


fun digestFromFile() {

    fun incrementalName(base: String, extension: String): String {
        fun regulation(i: Int): String {
            return if (i == 0) {
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

    val wb = createWorkbook(File("a.xlsx"))
    digest(wb, listOf())

    val fileOut = FileOutputStream(incrementalName("b", ".xlsx"))
    wb.write(fileOut)
    fileOut.flush()
    fileOut.close()

}


fun digestFromInputStream(inputStream: InputStream, sections: List<Rpc.Section>): ByteArrayOutputStream {

    val wb = WorkbookFactory.create(inputStream).apply {
        if (spreadsheetVersion == SpreadsheetVersion.EXCEL97) {
            throw CoreException.UnsupportedVersionException()
        }
    }
    digest(wb, sections)

    return ByteArrayOutputStream().also {
        wb.write(it)
    }
}

fun digest(wb: Workbook, sections: List<Rpc.Section>) {
    sections.fold(RpcExec(wb)) { a, b -> b.exec(a) }

//    wb.cloneSheet(wb.getSheet("Sheet1"), "hi1")
//    P().copySheet2(wb, wb.getSheet("Sheet1"), "hi21")
//    P().copySheet2(wb, wb.getSheet("Sheet2"), "hi22")

}

