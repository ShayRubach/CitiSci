package com.ezaf.www.citisci.data.conds

import com.ezaf.www.citisci.data.exp.DEFAULT_MAGNETIC_VALUE
import com.ezaf.www.citisci.data.exp.ExpCondition
import com.ezaf.www.citisci.data.exp.SharedDataHelper

class MagneticFieldExpCondition(private val minX: Float, private val maxX: Float,
                                private val minY: Float, private val maxY: Float,
                                private val minZ: Float, private val maxZ: Float) : ExpCondition {

    companion object {
        private const val MAG_FIELD_X_AXIS = 0
        private const val MAG_FIELD_Y_AXIS = 1
        private const val MAG_FIELD_Z_AXIS = 2

    }

    override fun isConditionMet(): Boolean {
        val magneticFields = SharedDataHelper.magneticFieldValues

        return  isInRange(minX, maxX, magneticFields[MAG_FIELD_X_AXIS]) &&
                isInRange(minY,maxY, magneticFields[MAG_FIELD_Y_AXIS]) &&
                isInRange(minZ,maxZ, magneticFields[MAG_FIELD_Z_AXIS])
    }

    override fun toString(): String {
        return "[$minX, $maxX]," +
                "[$minY, $maxY]," +
                "[$minZ, $maxZ]\n"
    }

    private fun isConsiderable(min: Float, max: Float): Boolean {
        return min.compareTo(DEFAULT_MAGNETIC_VALUE) != 0 || max.compareTo(DEFAULT_MAGNETIC_VALUE) != 0
    }

    private fun isInRange(min: Float, max: Float, value: Float): Boolean {
        return if(isConsiderable(min,max)){
            value in min..max
        }
        else true
    }

}