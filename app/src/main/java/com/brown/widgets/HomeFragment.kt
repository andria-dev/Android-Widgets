package com.brown.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.brown.widgets.helpers.NotifyHelper


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
	private val TAG = HomeFragment::class.simpleName
	private lateinit var makeToast: (String, Boolean) -> Unit
	private lateinit var manager: IManager

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		makeToast = NotifyHelper.toastGenerator(requireContext())
		manager = context as IManager

		// Get initial status
		updateView(manager.sendMonitorAction(ManagerAction.MONITORING_CHANGE))

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.home, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val toggleButton = view.findViewById<Button>(R.id.ToggleServiceButton)
		toggleButton.setOnClickListener {
			makeToast("Toggling monitoring", true)
			updateView(manager.sendMonitorAction(ManagerAction.TOGGLE_MONITORING))
		}
	}

	private fun updateView(status: ManagerStatus = ManagerStatus.UNKNOWN) {
		if (view == null) return
		makeToast("Updating HomeFragment view.", true)

		val toggleButton = requireView().findViewById<Button>(R.id.ToggleServiceButton)
		when (status) {
			ManagerStatus.MONITORING -> {
				toggleButton.text = resources.getText(R.string.stop_widget_service_text)
			}
			ManagerStatus.STOPPED -> {
				toggleButton.text = resources.getText(R.string.start_widget_service_text)
			}
			ManagerStatus.UNKNOWN -> {
				toggleButton.text = getString(R.string.toggle_widget_service)
			}
		}
	}
}