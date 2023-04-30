package eom.demo.ejh_board.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Pagination<T>(
    @JsonProperty("result") private var result: Collection<T>,
    private val page: Int,
    private val limit: Int,
    private val totalPages: Int,
) {
    fun getResult(): Collection<T> {
        return result
    }

    fun setResult(_result: Collection<T>) {
        this.result = _result
    }

    fun getPage(): Int {
        return page
    }

    fun getLimit(): Int {
        return limit
    }

    fun getTotalPages(): Int {
        return totalPages
    }

}
