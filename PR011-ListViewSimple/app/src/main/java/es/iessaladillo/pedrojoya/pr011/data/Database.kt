package es.iessaladillo.pedrojoya.pr011.data

import java.util.*

object Database {

    private val students = ArrayList<String>()

    fun queryStudents() = students

    fun addStudent(student: String) {
        students.add(student)
    }

    fun deleteStudent(position: Int) {
        students.removeAt(position)
    }

}
