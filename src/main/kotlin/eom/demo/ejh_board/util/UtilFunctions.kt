package eom.demo.ejh_board.util

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class UtilFunctions {

    fun epochMillisToDate(epochMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(Date(epochMillis))
    }

}
