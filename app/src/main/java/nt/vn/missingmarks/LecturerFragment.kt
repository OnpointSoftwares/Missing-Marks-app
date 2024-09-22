package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import nt.vn.missingmarks.adapters.LecturerAdapter
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Lecturer

class LecturerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lecturerAdapter: LecturerAdapter
    private lateinit var lecturerList: MutableList<Lecturer>
    private lateinit var database: DatabaseReference
    private lateinit var addLecturerButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lecturer, container, false)
        addLecturerButton = view.findViewById(R.id.newLecturer)
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.lecturerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        lecturerList = mutableListOf()
        addLecturerButton.setOnClickListener {
            showAddLecturerDialog()
        }
        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("lecturers")

        // Fetch and set the lecturer data from Firebase
        fetchLecturersFromFirebase()

        return view
    }

    private fun showAddLecturerDialog() {
        // Create a dialog to add lecturer
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_lecturer, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add New Lecturer")

        val alertDialog = builder.show()

        // Get references to the input fields in the dialog
        val editTextLecturerId = dialogView.findViewById<EditText>(R.id.editTextLecturerId)
        val editTextLecturerName = dialogView.findViewById<EditText>(R.id.editTextLecturerName)
        val editTextLecturerCourses = dialogView.findViewById<EditText>(R.id.editTextLecturerCourses)
        val submitButton = dialogView.findViewById<Button>(R.id.buttonSubmitLecturer)

        // Handle form submission
        submitButton.setOnClickListener {
            val lecturerId = editTextLecturerId.text.toString()
            val name = editTextLecturerName.text.toString()
            val coursesInput = editTextLecturerCourses.text.toString()
            val courses = coursesInput.split(",").map { courseName ->
                Course(courseId = "", courseName = courseName.trim())
            }.filter { it.courseName.isNotEmpty() }

            if (lecturerId.isNotEmpty() && name.isNotEmpty()) {
                // Create new lecturer object
                val newLecturer = Lecturer(
                    lecturerId = lecturerId,
                    name = name,
                    courses = courses
                )

                // Add new lecturer to Firebase
                val key = database.push().key.toString()
                database.child(key).setValue(newLecturer).addOnCompleteListener {
                    Toast.makeText(requireContext(), "Lecturer added", Toast.LENGTH_LONG).show()
                }

                // Dismiss the dialog
                alertDialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLecturersFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lecturerList.clear()

                for (lecturerSnapshot in dataSnapshot.children) {
                    val lecturerId = lecturerSnapshot.child("lecturerId").getValue(String::class.java) ?: ""
                    val name = lecturerSnapshot.child("name").getValue(String::class.java) ?: ""
                    val coursesList = mutableListOf<Course>()
                    for (courseSnapshot in lecturerSnapshot.child("courses").children) {
                        val courseName = courseSnapshot.child("courseName").getValue(String::class.java) ?: ""
                        coursesList.add(Course(courseName = courseName))
                    }

                    val lecturer = Lecturer(
                        lecturerId = lecturerId,
                        name = name,
                        courses = coursesList
                    )

                    lecturerList.add(lecturer)
                }

                lecturerAdapter = LecturerAdapter(lecturerList)
                recyclerView.adapter = lecturerAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LecturerFragment", "loadLecturer:onCancelled", databaseError.toException())
            }
        })
    }
}
