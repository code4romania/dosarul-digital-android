package ro.code4.casefile.data.helper

import androidx.room.TypeConverter
import ro.code4.casefile.ui.patient.addpatient.FamilyRelation
import ro.code4.casefile.ui.patient.addpatient.Gender
import ro.code4.casefile.ui.patient.addpatient.MaritalStatus

object EnumConverters {
    @TypeConverter
    @JvmStatic
    fun convertMaritalStatusToInt(maritalStatus: MaritalStatus?): Int? = maritalStatus?.id

    @TypeConverter
    @JvmStatic
    fun convertIntToMaritalStatus(id: Int?): MaritalStatus? = MaritalStatus.getMaritalStatusById(id)

    @TypeConverter
    @JvmStatic
    fun convertGenderToInt(gender: Gender?): Int? = gender?.id

    @TypeConverter
    @JvmStatic
    fun convertIntToGender(id: Int?): Gender? = Gender.getGenderById(id)

    @TypeConverter
    @JvmStatic
    fun convertFamilyRelationToInt(familyRelation: FamilyRelation?): Int? = familyRelation?.id

    @TypeConverter
    @JvmStatic
    fun convertIntToFamilyRelation(id: Int?): FamilyRelation? =
        FamilyRelation.getFamilyRelationById(id)
}
