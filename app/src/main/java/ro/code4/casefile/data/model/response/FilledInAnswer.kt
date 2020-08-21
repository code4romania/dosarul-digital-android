package ro.code4.casefile.data.model.response

import com.google.gson.annotations.Expose


class FilledInAnswer() {

    @Expose
    var idOption: Int = -1

    @Expose
    lateinit var text: String

    @Expose
    var isFreeText: Boolean = false

    @Expose
    var beneficiaryId: Int = -1

    @Expose
    var value: String? = null

}
