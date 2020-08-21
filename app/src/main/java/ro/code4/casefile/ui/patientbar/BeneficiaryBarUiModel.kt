
package ro.code4.casefile.ui.patientbar

data class BeneficiaryBarUiModel(val name: String,
                                 val canChange: Boolean = false,
                                 val listener: () -> Unit = {},
                                 val buttonVisibility: Boolean = true
)
