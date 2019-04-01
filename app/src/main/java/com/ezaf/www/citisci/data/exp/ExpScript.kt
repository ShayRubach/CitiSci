package com.ezaf.www.citisci.data.exp


class ExpScript(val actions: MutableList<ExpAction>)
{
    override fun toString(): String {
        return "$actions"
    }
}