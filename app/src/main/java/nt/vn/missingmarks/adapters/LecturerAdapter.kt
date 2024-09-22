package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Lecturer

class LecturerAdapter(
    private val lecturers: List<Lecturer>
) : RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder>() {

    inner class LecturerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lecturerNameTextView: TextView = itemView.findViewById(R.id.lecturerName)
        private val lecturerIdTextView: TextView = itemView.findViewById(R.id.lecturerId)
        private val viewCoursesButton: Button = itemView.findViewById(R.id.view_courses)

        fun bind(lecturer: Lecturer) {
            lecturerNameTextView.text = lecturer.name
            lecturerIdTextView.text = "ID: ${lecturer.lecturerId}"

            // Set click listener for viewing courses button
            viewCoursesButton.setOnClickListener {
                // Create a BottomSheetDialog instance
                val bottomSheetDialog = BottomSheetDialog(itemView.context)

                // Inflate the layout for the BottomSheetDialog
                val bottomSheetView = LayoutInflater.from(itemView.context).inflate(
                    R.layout.view_courses,
                    null
                )

                // Find the TextView in the BottomSheetDialog layout to display courses
                val coursesTextView: TextView = bottomSheetView.findViewById(R.id.coursesTextView)

                // Prepare a list of course names to display
                val coursesText = lecturer.courses.joinToString(separator = "\n") { it.courseName }

                // Set the courses text
                coursesTextView.text = coursesText

                // Set the inflated view as the content view for the dialog
                bottomSheetDialog.setContentView(bottomSheetView)

                // Show the BottomSheetDialog
                bottomSheetDialog.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lecturer, parent, false) // Updated to use lecturer item layout
        return LecturerViewHolder(view)
    }

    override fun onBindViewHolder(holder: LecturerViewHolder, position: Int) {
        holder.bind(lecturers[position])
    }

    override fun getItemCount(): Int = lecturers.size
}
