package org.d3ifcool.hystorms.ui.main.tank.tableview.model

import com.evrencoskun.tableview.sort.ISortableModel

data class CellModel(
    var mId: String = "",
    var obj: Any? = null
) : ISortableModel {
    override fun getId(): String = mId

    override fun getContent(): Any? = obj
}