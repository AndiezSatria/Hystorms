package org.d3ifcool.hystorms.util

interface ItemClickHandler<T> {
    fun onClick(item: T)
    fun onItemDelete(item: T)
}