package com.ezaf.www.citisci.data


class ExpScript(val actions: MutableList<ExpAction>)
{
    override fun toString(): String {
        return "$actions"
    }
}