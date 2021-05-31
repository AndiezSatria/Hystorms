package org.d3ifcool.hystorms.ui.main.encyclopedia

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EncyclopediaSectionAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val listPage = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        listPage.add(fragment)
    }

    override fun getItemCount(): Int {
        return listPage.size
    }

    override fun createFragment(position: Int): Fragment {
        return listPage[position]
    }
}