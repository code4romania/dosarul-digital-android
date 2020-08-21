package ro.code4.casefile.ui.patient.addpatient

import androidx.annotation.StringRes
import ro.code4.casefile.R

enum class MaritalStatus(val id: Int) {
    NOT_MARRIED(0),
    MARRIED(1),
    DIVORCED(2),
    WIDOWED(3);

    companion object {
        fun getMaritalStatusById(id: Int?): MaritalStatus? {
            return when (id) {
                NOT_MARRIED.id -> NOT_MARRIED
                MARRIED.id -> MARRIED
                DIVORCED.id -> DIVORCED
                WIDOWED.id -> WIDOWED
                else -> null
            }
        }
    }
}

fun MaritalStatus?.toUiMaritalStatus(): UiMaritalStatus {
    return when (this) {
        MaritalStatus.NOT_MARRIED -> UiMaritalStatus.NOT_MARRIED
        MaritalStatus.MARRIED -> UiMaritalStatus.MARRIED
        MaritalStatus.DIVORCED -> UiMaritalStatus.DIVORCED
        MaritalStatus.WIDOWED -> UiMaritalStatus.WIDOWED
        else -> UiMaritalStatus.UNDEFINED
    }
}

fun UiMaritalStatus.toMaritalStatus(): MaritalStatus? {
    return when (this) {
        UiMaritalStatus.NOT_MARRIED -> MaritalStatus.NOT_MARRIED
        UiMaritalStatus.MARRIED -> MaritalStatus.MARRIED
        UiMaritalStatus.DIVORCED -> MaritalStatus.DIVORCED
        UiMaritalStatus.WIDOWED -> MaritalStatus.WIDOWED
        else -> null
    }
}

enum class UiMaritalStatus(@StringRes val stringRes: Int) {
    UNDEFINED(R.string.undefined),
    NOT_MARRIED(R.string.not_married),
    MARRIED(R.string.married),
    DIVORCED( R.string.divorced),
    WIDOWED(R.string.widowed);
}


enum class Gender(val id: Int, @StringRes val stringRes: Int) {
    MALE(0, R.string.male),
    FEMALE(1, R.string.female);

    companion object {
        fun getGenderById(id: Int?): Gender? {
            return when (id) {
                FEMALE.id -> FEMALE
                MALE.id -> MALE
                else -> null
            }
        }
    }
}

enum class FamilyRelation(val id: Int, @StringRes val stringRes: Int) {
    CHILD(0, R.string.child),
    MOTHER(1, R.string.mother),
    SISTER(2, R.string.sister);

    companion object {
        fun getFamilyRelationById(id: Int?): FamilyRelation? {
            return when (id) {
                CHILD.id -> CHILD
                MOTHER.id -> MOTHER
                SISTER.id -> SISTER
                else -> null
            }
        }
    }
}
