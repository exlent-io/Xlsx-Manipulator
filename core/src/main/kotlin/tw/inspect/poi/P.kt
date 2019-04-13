package tw.inspect.poi

import org.apache.poi.EncryptedDocumentException
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ooxml.POIXMLDocumentPart
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFDrawing
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


class P {

    @Throws(CoreException.DuplicatedSheetNameException::class)
    fun copySheet2(wb: Workbook, srcSheet: Sheet, dstSheetName: String): Sheet {

        if (wb.getSheet(dstSheetName) != null) {
            throw CoreException.DuplicatedSheetNameException()
        }

        val dstSheet = wb.createSheet(dstSheetName)
        dstSheet.defaultRowHeight = srcSheet.defaultRowHeight
        copyRows(
            srcSheet,
            dstSheet,
            srcSheet.firstRowNum,
            srcSheet.lastRowNum,
            0,
            true,
            AdjustRowHeight.ADJUST_LEAVE_EMPTY_ALONE
        )

        return dstSheet
    }


}


fun copyRows(
    srcSheet: Sheet,
    dstSheet: Sheet,
    srcFirstRow: Int,
    srcLastRow: Int,
    dstFirstRow: Int,
    adjustColumnWidth: Boolean,
    adjustEmptyRowHeight: AdjustRowHeight
) {
    if (srcFirstRow < 0 || srcLastRow < 0 || srcLastRow < srcFirstRow) {
        throw CoreException.OutOfRangeException()
    }

    val lastColumn = (srcFirstRow..srcLastRow).fold(-1) { acc, i ->
        srcSheet.getRow(i)?.run {
            max(lastCellNum.toInt(), acc)
        } ?: acc
    }

    copyRange(
        srcSheet,
        dstSheet,
        srcFirstRow,
        srcLastRow,
        0,
        lastColumn,
        dstFirstRow,
        0,
        adjustColumnWidth,
        adjustEmptyRowHeight
    )


    //dstSheet.getXSSFDrawing()?.importChart()
    fun getXSSFDrawing(xssfSheet: XSSFSheet): XSSFDrawing? {
        for (rp in xssfSheet.relationParts) {
            val r: POIXMLDocumentPart = rp.getDocumentPart()
            if (r is XSSFDrawing) {
                return r
            }
        }
        return null
    }
    getXSSFDrawing(dstSheet as XSSFSheet)?.apply {
        println(charts)
        println(shapes)
        shapes.forEach {
            copySimpleShape(it, dstSheet)
        }
    }
}


enum class AdjustRowHeight(private val value: Int) {
    NONE(0), ADJUST_LEAVE_EMPTY_ALONE(1), ADJUST_SET_EMPTY_SRC_DEFAULT(2);

    fun adjustRowHeight() = value.and(1) == 1
    fun setEmptySrcDefault() = value.and(2) == 1
}

enum class MergeMode {
    OVERRIDE_ALL, SKIP_EMPTY
}

private fun copyRange(
    srcSheet: Sheet,
    dstSheet: Sheet,
    srcFirstRow: Int,
    srcLastRow: Int,
    srcFirstColumn: Int,
    srcLastColumn: Int,
    dstFirstRow: Int,
    dstFirstColumn: Int,
    adjustColumnWidth: Boolean = false,
    adjustRowHeight: AdjustRowHeight = AdjustRowHeight.NONE,
    copyFormat: Boolean = true
) {
    if (srcFirstRow < 0 || srcLastRow < 0 || srcFirstColumn < 0 || srcLastColumn < 0 ||
        srcLastRow < srcFirstRow || srcLastColumn < srcFirstColumn ||
        dstFirstRow < 0 || dstFirstColumn < 0
    ) {
        throw CoreException.OutOfRangeException()
    }

    fun removeDstMergedRegion() {
        TODO()
    }

    fun copyFormat() {
        srcSheet.mergedRegions.forEach { region ->
            if (region.firstRow >= srcFirstRow && region.lastRow <= srcLastRow && region.firstColumn >= srcFirstColumn && region.lastColumn <= srcLastColumn) {
                dstSheet.addMergedRegion(
                    CellRangeAddress(
                        dstFirstRow - srcFirstRow + region.firstRow,
                        dstFirstRow - srcFirstRow + region.lastRow,
                        dstFirstColumn - srcFirstColumn + region.firstColumn,
                        dstFirstColumn - srcFirstColumn + region.lastColumn
                    )
                )
            }
        }
    }

    fun adjustColumnWidth() {
        (srcFirstColumn until srcLastColumn).forEach {
            dstSheet.setColumnWidth(it + dstFirstColumn, srcSheet.getColumnWidth(it))
            dstSheet.setColumnHidden(it + dstFirstColumn, srcSheet.isColumnHidden(it))
        }
    }

    fun copyDraw() {
        srcSheet.copyAllDraw(dstSheet)
    }


    fun copyCells() {
        (srcFirstRow..srcLastRow).forEachIndexed { forEachIndex, srcRowIndex ->
            val srcRow: Row? = srcSheet.getRow(srcRowIndex)

            if (srcRow == null) {
                if (adjustRowHeight.setEmptySrcDefault()) {
                    val dstRow =
                        dstSheet.getRow(forEachIndex + dstFirstRow) ?: dstSheet.createRow(forEachIndex + dstFirstRow)!!
                    dstRow.height = srcSheet.defaultRowHeight
                }
                return@forEachIndexed
            }

            val dstRow = dstSheet.getRow(forEachIndex + dstFirstRow) ?: dstSheet.createRow(forEachIndex + dstFirstRow)!!

            if (adjustRowHeight.adjustRowHeight()) {
                dstRow.height = srcRow.height
                dstRow.zeroHeight = srcRow.zeroHeight
            }
            for (j in max(srcRow.firstCellNum.toInt(), srcFirstColumn) until min(
                srcRow.lastCellNum.toInt(),
                srcLastColumn
            )) {
                val fromCell = srcRow.getCell(j) ?: continue

                val newCell = dstRow.createCell(j)!!
                newCell.cellStyle = fromCell.cellStyle

                newCell.cellType = fromCell.cellType

                val cType = if (fromCell.cellType == CellType.FORMULA) {
                    // TODO  improve - update formula reference ?

                    /** @return one of ({@link CellType#NUMERIC}, {@link CellType#STRING},
                     *     {@link CellType#BOOLEAN}, {@link CellType#ERROR}) depending
                     * on the cached value of the formula
                     */
                    newCell.cellFormula = fromCell.cellFormula
                    fromCell.cachedFormulaResultType
                } else {
                    fromCell.cellType
                }

                when (cType) {
                    CellType.NUMERIC -> newCell.setCellValue(fromCell.numericCellValue)
                    CellType.STRING -> newCell.setCellValue(fromCell.richStringCellValue)
                    CellType.BOOLEAN -> newCell.setCellValue(fromCell.booleanCellValue)
                    CellType.FORMULA -> {
                    }
                    // TODO add flag to toggle ignore blank, may be useful for avoid override exist value
                    CellType.BLANK -> newCell.setCellValue(fromCell.richStringCellValue)
                    CellType.ERROR -> newCell.setCellValue(fromCell.errorCellValue.toDouble())
                    else -> newCell.setCellValue(fromCell.richStringCellValue)
                }
            }
        }
    }


    //removeDstMergedRegion()
    if (copyFormat) {
        copyFormat()
        copyDraw()
    }
    if (adjustColumnWidth) {
        adjustColumnWidth()
    }
    copyCells()


}

object ExcelOperationUtil {

    /**
     * sheet 复制，复制数据、样式<br></br>
     *
     * <br></br>建议用于 不同book间复制，同时复制数据和样式<br></br>
     * eg: cloneSheet(srcSheet, desSheet, mapping)
     *
     * @param mapping 不同文件间复制时，如果要复制样式，必传，否则不复制样式
     */
    fun copySheet(srcSheet: Sheet, desSheet: Sheet, mapping: StyleMapping) {
        copySheet(srcSheet, desSheet, true, true, mapping)
    }

    /**
     * sheet 复制,复制数据<br></br>
     *
     * <br></br>建议用于 同book中，只复制数据，不复制样式<br></br>
     * eg: cloneSheet(srcSheet, desSheet, false, null)
     *
     * @param srcSheet
     * @param desSheet
     * @param copyStyleFlag
     * @param mapping
     */
    fun copySheet(srcSheet: Sheet, desSheet: Sheet, copyStyleFlag: Boolean, mapping: StyleMapping) {
        copySheet(srcSheet, desSheet, true, copyStyleFlag, mapping)
    }

    /**
     * sheet 复制, 灵活控制是否控制数据、样式<br></br>
     *
     * <br></br>不建议直接使用
     *
     * @param copyValueFlag 控制是否复制数据
     * @param copyStyleFlag 控制是否复制样式
     * @param mapping       不同book中复制样式时，必传
     */
    @JvmOverloads
    fun copySheet(
        srcSheet: Sheet,
        desSheet: Sheet,
        copyValueFlag: Boolean = true,
        copyStyleFlag: Boolean = true,
        mapping: StyleMapping? = null
    ) {
        if (srcSheet.workbook === desSheet.workbook) {
            //logger.warn("统一workbook内复制sheet建议使用 workbook的cloneSheet方法")
        }

        //合并区域处理
        copyMergedRegion(srcSheet, desSheet)

        //行复制
        val rowIterator = srcSheet.rowIterator()

        var areadlyColunm = 0
        while (rowIterator.hasNext()) {
            val srcRow = rowIterator.next()
            val desRow = desSheet.createRow(srcRow.rowNum)
            copyRow(srcRow, desRow, copyValueFlag, copyStyleFlag, mapping)

            //调整列宽(增量调整)
            if (srcRow.physicalNumberOfCells > areadlyColunm) {
                for (i in areadlyColunm until srcRow.physicalNumberOfCells) {
                    desSheet.setColumnWidth(i, srcSheet.getColumnWidth(i))
                }
                areadlyColunm = srcRow.physicalNumberOfCells
            }
        }
    }

    /**
     * 复制行
     */
    fun copyRow(srcRow: Row, desRow: Row, mapping: StyleMapping) {
        copyRow(srcRow, desRow, true, true, mapping)
    }

    /**
     * 复制行
     */
    fun copyRow(srcRow: Row, desRow: Row, copyStyleFlag: Boolean, mapping: StyleMapping) {
        copyRow(srcRow, desRow, true, copyStyleFlag, mapping)
    }

    /**
     * 复制行
     */
    @JvmOverloads
    fun copyRow(
        srcRow: Row,
        desRow: Row,
        copyValueFlag: Boolean = true,
        copyStyleFlag: Boolean = true,
        mapping: StyleMapping? = null
    ) {
        val it = srcRow.cellIterator()
        while (it.hasNext()) {
            val srcCell = it.next()
            val desCell = desRow.createCell(srcCell.columnIndex)
            copyCell(srcCell, desCell, copyValueFlag, copyStyleFlag, mapping)
        }
    }

    /**
     * 复制区域（合并单元格）
     */
    fun copyMergedRegion(srcSheet: Sheet, desSheet: Sheet) {
        val sheetMergerCount = srcSheet.numMergedRegions
        for (i in 0 until sheetMergerCount) {
            desSheet.addMergedRegion(srcSheet.getMergedRegion(i))
            val cellRangeAddress = srcSheet.getMergedRegion(i)
        }
    }

    /**
     * 复制单元格，复制数据,复制样式
     * @param mapping       不同文件间复制时，如果要复制样式，必传，否则不复制样式
     */
    fun copyCell(srcCell: Cell, desCell: Cell, mapping: StyleMapping) {
        copyCell(srcCell, desCell, true, true, mapping)
    }

    /**
     * 复制单元格，复制数据
     * @param copyStyleFlag 控制是否复制样式
     * @param mapping       不同文件间复制时，如果要复制样式，必传，否则不复制样式
     */
    fun copyCell(srcCell: Cell, desCell: Cell, copyStyleFlag: Boolean, mapping: StyleMapping) {
        copyCell(srcCell, desCell, true, copyStyleFlag, mapping)
    }

    /**
     * 复制单元格
     * @param copyValueFlag 控制是否复制单元格的内容
     * @param copyStyleFlag 控制是否复制样式
     * @param mapping 不同文件间复制时，如果需要连带样式复制，必传，否则不复制样式
     */
    @JvmOverloads
    fun copyCell(
        srcCell: Cell,
        desCell: Cell,
        copyValueFlag: Boolean = true,
        copyStyleFlag: Boolean = true,
        mapping: StyleMapping? = null
    ) {
        val srcBook = srcCell.sheet.workbook
        val desBook = desCell.sheet.workbook

        //复制样式
        //如果是同一个excel文件内，连带样式一起复制
        if (srcBook === desBook && copyStyleFlag) {
            //同文件，复制引用
            desCell.cellStyle = srcCell.cellStyle
        } else if (copyStyleFlag) {
            //不同文件，通过映射关系复制
            if (null != mapping) {
                val desIndex = mapping.desIndex(srcCell.cellStyle.index)
                desCell.cellStyle = desBook.getCellStyleAt(desIndex.toInt())
            }
        }

        //复制评论
        if (srcCell.cellComment != null) {
            desCell.cellComment = srcCell.cellComment
        }

        //复制内容
        desCell.cellType = srcCell.cellType

        if (copyValueFlag) {
            when (srcCell.cellType) {
                CellType.NUMERIC -> desCell.setCellValue(srcCell.numericCellValue)
                CellType.STRING -> desCell.setCellValue(srcCell.richStringCellValue)
                CellType.BOOLEAN -> desCell.setCellValue(srcCell.booleanCellValue)
                CellType.FORMULA -> desCell.cellFormula = srcCell.cellFormula
                CellType.BLANK -> {
                    // TODO not sure
//                    desCell.setCellValue(srcCell.richStringCellValue)
                }
                CellType.ERROR -> desCell.setCellValue(srcCell.errorCellValue.toDouble())
                else -> {
                    // TODO not sure
//                    desCell.setCellValue(srcCell.richStringCellValue)
                }
            }
        }

    }


    /**
     * 把一个excel中的styleTable复制到另一个excel中<br></br>
     * 如果是同一个excel文件，就不用复制styleTable了
     * @return StyleMapping 两个文件中styleTable的映射关系
     * @see StyleMapping
     */
    fun copyCellStyle(srcBook: Workbook?, desBook: Workbook?): StyleMapping {
        if (null == srcBook || null == desBook) {
            throw ExcelException("源excel 或 目标excel 不存在")
        }
        if (srcBook == desBook) {
            throw ExcelException("不要使用此方法在同一个文件中copy style，同一个excel中复制sheet不需要copy Style")
        }
        if (srcBook is HSSFWorkbook && desBook is XSSFWorkbook || srcBook is XSSFWorkbook && desBook is HSSFWorkbook) {
            throw ExcelException("不支持在不同的版本的excel中复制样式）")
        }

        //logger.debug("src中style number:{}, des中style number:{}", srcBook.numCellStyles, desBook.numCellStyles)
        val src2des = ShortArray(srcBook.numCellStyles)
        val des2src = ShortArray(desBook.numCellStyles + srcBook.numCellStyles)

        for (i in 0 until srcBook.numCellStyles) {
            //建立双向映射
            val srcStyle = srcBook.getCellStyleAt(i)
            val desStyle = desBook.createCellStyle()
            src2des[srcStyle.index.toInt()] = desStyle.index
            des2src[desStyle.index.toInt()] = srcStyle.index

            //复制样式
            desStyle.cloneStyleFrom(srcStyle)
        }


        return StyleMapping(des2src, src2des)
    }

    /**
     * 存放两个excel文件中的styleTable的映射关系，以便于在复制表格时，在目标文件中获取到对应的样式
     */
    class StyleMapping(private val des2srcIndexMapping: ShortArray, private val src2desIndexMapping: ShortArray) {

        fun srcIndex(desIndex: Short): Short {
            if (desIndex < 0 || desIndex >= this.des2srcIndexMapping.size) {
                throw ExcelException("索引越界：源文件styleNum=" + this.des2srcIndexMapping.size + " 访问位置=" + desIndex)
            }
            return this.des2srcIndexMapping[desIndex.toInt()]
        }

        /**
         * 根据源文件的style的index,获取目标文件的style的index
         * @param srcIndex 源excel中style的index
         * @return desIndex 目标excel中style的index
         */
        fun desIndex(srcIndex: Short): Short {
            if (srcIndex < 0 || srcIndex >= this.src2desIndexMapping.size) {
                throw ExcelException("索引越界：源文件styleNum=" + this.src2desIndexMapping.size + " 访问位置=" + srcIndex)
            }

            return this.src2desIndexMapping[srcIndex.toInt()]
        }
    }

}

class ExcelException(message: String) : RuntimeException(message)


@Throws(IOException::class, EncryptedDocumentException::class, CoreException.UnsupportedVersionException::class)
fun createWorkbook(file: File): Workbook {
    //如果文件不存在，抛出文件没找到异常
    FileInputStream(file).use {
        return WorkbookFactory.create(it).apply {
            if (spreadsheetVersion == SpreadsheetVersion.EXCEL97) {
                throw CoreException.UnsupportedVersionException()
            }
        }
    }

}

open class CoreException : Exception() {
    class UnsupportedVersionException : CoreException()
    class DuplicatedSheetNameException : CoreException()
    class OutOfRangeException : CoreException()
}
