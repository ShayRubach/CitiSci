package com.ezaf.www.citisci.data

import androidx.room.*
import com.ezaf.www.citisci.MainActivity.Companion.db
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.TypeConverterUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.time.Instant

@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
class Experiment (
        @PrimaryKey
        var _id: String,
        @Embedded
        var basicData: ExpBasicData,
        @TypeConverters(TypeConverterUtil::class)
        var actionIdList: MutableList<String> = mutableListOf()
) {
        @Ignore
        val actions: MutableList<ExpAction> = mutableListOf()

        constructor() : this("DEF_ID",ExpBasicData("DEF_BD_ID","", Instant.now(),false,"",""), mutableListOf())

        init {
                publishExpId()
                attachActions()
        }

        private fun attachActions() {
                Observable.fromCallable {
                        db.expActionsDao()
                }.doOnNext {
                        for(actionID in actionIdList){
                                actions.add(it.getActionById(actionID))
                        }
                }.doOnComplete {
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
        }

        private fun publishExpId() {
                basicData.consumeExpIdOnce(_id)
                for(a in actions ){
                        a.consumeExpIdOnce(_id)
                }

        }

        override fun toString(): String {
                var builder = StringBuilder("$_id\n$basicData\n")
                for(a in actions){
                        builder.append("$a\n")
                }

                return builder.toString()
        }
}
