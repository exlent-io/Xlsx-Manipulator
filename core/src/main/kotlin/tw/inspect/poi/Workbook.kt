package tw.inspect.poi

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

@Throws(CoreException.DuplicatedSheetNameException::class)
fun Workbook.cloneSheet(sourceSheet: Sheet, destSheetName: String): Sheet {
    if (getSheet(destSheetName) != null) {
        throw CoreException.DuplicatedSheetNameException()
    }
    val sheet = cloneSheet(getSheetIndex(sourceSheet))
    setSheetName(getSheetIndex(sheet), destSheetName)
    return sheet
}