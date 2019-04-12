package tw.inspect.poi

import org.apache.poi.ooxml.POIXMLDocumentPart
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.*


fun Sheet.copyAllDraw(destSheet: Sheet) {
    if (this is XSSFSheet && destSheet is XSSFSheet) {
        copyAllDraw(destSheet)
    } else {
        TODO()
    }
}


fun Sheet.getXSSFDrawing(): XSSFDrawing? {
    if (this is XSSFSheet) {
        return getXSSFDrawing()
    } else {
        TODO()
    }
}

fun XSSFSheet.getXSSFDrawing(): XSSFDrawing? {
    for (rp in relationParts) {
        val r: POIXMLDocumentPart = rp.getDocumentPart()
        if (r is XSSFDrawing) {
            return r
        }
    }
    return null
}

fun copySimpleShape(shape: XSSFShape, newSheet: XSSFSheet) {
    if (shape !is XSSFSimpleShape) {
        return
    }
    val simpleShape = shape
    val anchor = shape.getAnchor() as XSSFClientAnchor

    val col1 = anchor.col1.toInt()
    val col2 = anchor.col2.toInt()
    val row1 = anchor.row1
    val row2 = anchor.row2

    val x1 = anchor.dx1
    val x2 = anchor.dx2
    val y1 = anchor.dy1
    val y2 = anchor.dy2

    val newWb = newSheet.workbook
    val newHelper = newWb.creationHelper
    val newDrawing: XSSFDrawing? = newSheet.createDrawingPatriarch()//?:return
    val newAnchor = newHelper.createClientAnchor()//?:return


    // Row / Column placement.
    newAnchor.setCol1(col1)
    newAnchor.setCol2(col2)
    newAnchor.row1 = row1
    newAnchor.row2 = row2

    // Fine touch adjustment along the XY coordinate.
    newAnchor.dx1 = x1
    newAnchor.dx2 = x2
    newAnchor.dy1 = y1
    newAnchor.dy2 = y2

    val dstSimpleShape = newDrawing?.createSimpleShape(
        //XSSFClientAnchor(0, 0, 0, 0, 26, 26, 30, 30)
        XSSFClientAnchor(
            newAnchor.dx1,
            newAnchor.dy1,
            newAnchor.dx2,
            newAnchor.dy1,
            newAnchor.col1.toInt(),
            newAnchor.row1,
            newAnchor.col2.toInt(),
            newAnchor.row2
        )
    )

    //dstSimpleShape.shapeType = simpleShape.shapeType
    //dstSimpleShape.bottomInset = simpleShape.bottomInset
    dstSimpleShape?.text = simpleShape.text

}


fun copyPicture(shape: XSSFShape, newSheet: XSSFSheet) {
    val picture = shape as XSSFPicture

    val xssfPictureData = picture.pictureData
    val anchor = shape.getAnchor() as XSSFClientAnchor

    val col1 = anchor.col1.toInt()
    val col2 = anchor.col2.toInt()
    val row1 = anchor.row1
    val row2 = anchor.row2

    val x1 = anchor.dx1
    val x2 = anchor.dx2
    val y1 = anchor.dy1
    val y2 = anchor.dy2

    val newWb = newSheet.workbook
    val newHelper = newWb.creationHelper
    val newDrawing = newSheet.createDrawingPatriarch()
    val newAnchor = newHelper.createClientAnchor()

    // Row / Column placement.
    newAnchor.setCol1(col1)
    newAnchor.setCol2(col2)
    newAnchor.row1 = row1
    newAnchor.row2 = row2

    // Fine touch adjustment along the XY coordinate.
    newAnchor.dx1 = x1
    newAnchor.dx2 = x2
    newAnchor.dy1 = y1
    newAnchor.dy2 = y2

    val newPictureIndex = newWb.addPicture(xssfPictureData.data, xssfPictureData.pictureType)

    val newPicture = newDrawing.createPicture(newAnchor, newPictureIndex)
}

fun XSSFSheet.copyAllDraw(dstSheet: XSSFSheet) {
    val wb: XSSFWorkbook = dstSheet.workbook

    val ct = dstSheet.ctWorksheet
    if (ct.isSetLegacyDrawing) {
        //FileInformationBlock.logger.log(POILogger.WARN, "Cloning sheets with comments is not yet supported.")
        ct.unsetLegacyDrawing()
    }
    if (ct.isSetPageSetup) {
        //FileInformationBlock.logger.log(POILogger.WARN, "Cloning sheets with page setup is not yet supported.")
        ct.unsetPageSetup()
    }

    val dg: XSSFDrawing? = getXSSFDrawing()

    // clone the sheet drawing along with its relationships
    if (dg != null) {
        if (ct.isSetDrawing) {
            // unset the existing reference to the drawing,
            // so that subsequent call of clonedSheet.createDrawingPatriarch() will create a new one
            ct.unsetDrawing()
        }
        var clonedDg = dstSheet.createDrawingPatriarch()
        // copy drawing contents
        clonedDg.ctDrawing.set(dg.ctDrawing)

        clonedDg = dstSheet.createDrawingPatriarch()

        // Clone drawing relations
        val srcRels = createDrawingPatriarch().relationParts
//        for (rp in srcRels) {
//            val method = XSSFWorkbook::class.java.getDeclaredMethod("addRelation")
//            method.isAccessible = true
//            method.invoke(wb, rp, clonedDg)
//        }
    }
}

fun Sheet.deleteRows(ranges: List<IntRange>) {

    ranges.forEach {
        if (it.first < 0 || it.last > lastRowNum) {
            throw RuntimeException("Out of scope")
        }
    }

    val mergedRanges = ranges.sortedBy { it.first }.fold(mutableListOf<IntRange>()) { acc, intRange ->
        acc.apply {
            if (isEmpty() || this[lastIndex].last + 1 < intRange.first) {
                add(intRange)
            } else {
                this[lastIndex] = this[lastIndex].first..kotlin.math.max(this[lastIndex].last, intRange.last)
            }
        }
    }

    //TODO 檢查合併儲存格

    // TODO improve remove
//        val keepRanges = mergedRanges.fold {
//
//        }

    val lastRowNum = lastRowNum
    mergedRanges.forEach {
        if (it.last < lastRowNum) {
            shiftRows(it.last + 1, lastRowNum, -(it.last - it.start + 1))
        } else {
            it.forEach { rowNo ->
                getRow(rowNo)?.apply { removeRow(this) }
            }
        }
    }
}