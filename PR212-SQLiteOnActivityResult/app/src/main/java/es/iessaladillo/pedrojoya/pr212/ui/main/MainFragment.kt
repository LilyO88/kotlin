package es.iessaladillo.pedrojoya.pr212.ui.main

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.iessaladillo.pedrojoya.pr212.R
import es.iessaladillo.pedrojoya.pr212.data.Repository
import es.iessaladillo.pedrojoya.pr212.data.RepositoryImpl
import es.iessaladillo.pedrojoya.pr212.data.local.DbHelper
import es.iessaladillo.pedrojoya.pr212.data.local.StudentDao
import es.iessaladillo.pedrojoya.pr212.data.model.Student
import es.iessaladillo.pedrojoya.pr212.extensions.toast
import es.iessaladillo.pedrojoya.pr212.ui.main.MainFragmentAdapter.OnItemClickListener
import es.iessaladillo.pedrojoya.pr212.ui.student.StudentActivity
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.ref.WeakReference

private const val RC_ADD_STUDENT = 1
private const val RC_EDIT_STUDENT = 2

class MainFragment : Fragment() {

    private lateinit var fab: FloatingActionButton
    private lateinit var mAdapter: MainFragmentAdapter
    private lateinit var repository: Repository
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        repository = RepositoryImpl(StudentDao(requireContext(), DbHelper
                .getInstance(requireContext())))
        viewModel = ViewModelProviders.of(requireActivity(),
                MainActivityViewModelFactory(repository)).get(MainActivityViewModel::class.java)
        initViews()
        if (savedInstanceState != null) {
            mAdapter.setData(viewModel.getStudents(false))
        } else {
            loadStudents()
        }
    }

    private fun initViews() {
        setupFab()
        setupRecyclerView()
    }

    private fun setupFab() {
        fab = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener { addStudent() }
    }

    private fun setupRecyclerView() {
        mAdapter = MainFragmentAdapter()
        with(mAdapter) {
            setOnItemClickListener(object: OnItemClickListener {
                override fun onItemClick(view: View, student: Student, position: Int) {
                    editStudent(student)
                }
            })
            setEmptyView(lblEmptyView)
        }
        with(lstStudents) {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,
                    false)
            addItemDecoration(DividerItemDecoration(requireActivity(),
                    LinearLayoutManager.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            val itemTouchHelper = ItemTouchHelper(
                    object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                            ItemTouchHelper.RIGHT) {
                        override fun onMove(recyclerView: RecyclerView,
                                            viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            deleteStudent(viewHolder.adapterPosition)
                        }
                    })
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun addStudent() {
        StudentActivity.startForResult(this, RC_ADD_STUDENT)
    }

    private fun editStudent(student: Student) {
        StudentActivity.startForResult(this, student.id, RC_EDIT_STUDENT)
    }

    private fun deleteStudent(position: Int) {
        DeleteStudentTask(this, repository).execute(mAdapter.getItemAtPosition(position))
    }

    private fun loadStudents() {
        LoadStudentsTask(this, viewModel).execute()
    }

    private fun showSuccessDeletingStudent() {
        toast(R.string.main_fragment_student_deleted)
    }

    private fun showErrorDeletingStudent() {
        toast(R.string.main_fragment_error_deleting_student)
    }

    override fun onDestroy() {
        mAdapter.onDestroy()
        repository.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == RC_ADD_STUDENT || requestCode == RC_EDIT_STUDENT) &&
                resultCode == Activity.RESULT_OK) {
            loadStudents()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private class LoadStudentsTask internal constructor(mainFragment: MainFragment, private val viewModel: MainActivityViewModel) : AsyncTask<Void, Void, List<Student>>() {

        private val mainFragmentWeakReference: WeakReference<MainFragment> = WeakReference(mainFragment)

        override fun doInBackground(vararg args: Void): List<Student> {
            return viewModel.getStudents(true)
        }

        override fun onPostExecute(students: List<Student>) {
            mainFragmentWeakReference.get()?.mAdapter?.setData(students)
        }

    }

    private class DeleteStudentTask internal constructor(mainFragment: MainFragment,
                                                         private val repository: Repository) :
            AsyncTask<Student, Void, Boolean>() {

        private val mainFragmentWeakReference = WeakReference(mainFragment)

        override fun doInBackground(vararg students: Student): Boolean {
            return repository.deleteStudent(students[0].id)
        }

        override fun onPostExecute(success: Boolean) {
            val mainFragment = mainFragmentWeakReference.get()
            if (success) {
                mainFragment?.showSuccessDeletingStudent()
                mainFragment?.loadStudents()
            } else {
                mainFragment?.showErrorDeletingStudent()
            }
        }

    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }

    }

}
