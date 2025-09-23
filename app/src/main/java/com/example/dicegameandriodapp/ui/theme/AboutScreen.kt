package com.example.dicegameandriodapp.ui.theme

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About") },
        text = {
            Text(
                "Student ID: 20210372 | W1867117\n" +
                        "Name: Lakisha Tharindi Kaluthotage\n" +
                        "I confirm that I understand what plagiarism is and have read and " +
                        "understood the section on Assessment Offences in the Essential " +
                        "Information for Students. The work that I have submitted is " +
                        "entirely my own. Any work from other authors is duly referenced " +
                        "and acknowledged.",
                fontSize = 12.sp
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
