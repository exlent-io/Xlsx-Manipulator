package tw.inspect.poi


import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.ss.usermodel.Workbook


class RpcExec(val wb: Workbook){

    fun exec(rpc: Rpc.Section) :RpcExec{
        println( ObjectMapper().writeValueAsString(rpc))

        when(rpc) {
            is Rpc.AddSheet -> {
                wb.createSheet(rpc.name)
                wb.setSheetOrder(rpc.name, rpc.order.toInt())

            }
            is Rpc.DeleteSheet -> {
                wb.removeSheetAt(wb.getSheetIndex(wb.getSheet(rpc.name)))
            }
            is Rpc.RenameSheet -> {
                wb.setSheetName(wb.getSheetIndex(wb.getSheet(rpc.oldName)), rpc.newName)
            }
            is Rpc.CopyRows -> {
                val srcSheet = wb.getSheet(rpc.srcSheet)
                val dstSheet = wb.getSheet(rpc.dstSheet)

                val srcRowRange = "^(.*)~(.*)$".toRegex().matchEntire(rpc.srcRowRange)!!
                    .destructured
                    .let {(a, b) ->
                        Pair(a.toInt(), b.toInt())
                    }

                println("${srcRowRange.first} ${srcRowRange.second} to ${rpc.dstRow.toInt() -1}")

                /**
                 * Turn 1-based to 0-based
                 * */
                copyRows(
                    srcSheet,
                    dstSheet,
                    srcRowRange.first -1,
                    srcRowRange.second -1,
                    rpc.dstRow.toInt() -1,
                    true,
                    AdjustRowHeight.ADJUST_LEAVE_EMPTY_ALONE
                )
            }
            is Rpc.Fill -> {
                //wb.getSheet(rpc.sheet).g
            }
        }
        return this
    }

}
