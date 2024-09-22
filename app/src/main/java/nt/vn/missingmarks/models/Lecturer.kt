package nt.vn.missingmarks.models

data class Lecturer(
    val lecturerId: String = "",
    val name: String = "",
    val courses: List<Course> = emptyList() // List of courses this lecturer teaches
)
