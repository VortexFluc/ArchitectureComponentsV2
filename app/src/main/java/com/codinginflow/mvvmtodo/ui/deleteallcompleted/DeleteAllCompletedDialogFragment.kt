package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment: DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Подтвердить удаление")
            .setMessage("Вы действительно хотите удалить все завершённые задачи")
            .setNegativeButton("Отменить", null)
            .setPositiveButton("Да") { _, _ ->
                viewModel.onConfirmClick()
            }
            .create()
}