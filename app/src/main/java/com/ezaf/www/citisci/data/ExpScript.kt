package com.ezaf.www.citisci.data

class ExpScript(val expActions: MutableList<ExpAction>,
                val expConditions: MutableList<ExpCondition>)
{
    override fun toString(): String {
        return "$expActions\n$expConditions"
    }
}