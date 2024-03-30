import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomArrayAdapter<T>(context: Context, resource: Int, objects: Array<T>) : ArrayAdapter<T>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view as? TextView
        if (position == 0) {
            textView?.setTextColor(Color.parseColor("#6D7DFF"))
        } else {
            textView?.setTextColor(Color.BLACK) // Color for other items
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as? TextView

        if (position == 0) {
            textView?.setTextColor(Color.parseColor("#6D7DFF"))
        } else {
            textView?.setTextColor(Color.BLACK)
        }

        return view
    }
}
