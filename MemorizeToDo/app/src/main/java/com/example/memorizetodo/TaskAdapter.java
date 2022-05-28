package com.example.memorizetodo;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import com.example.memorizetodo.database.TaskEntry;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    //Content for date format
    private static final String DATE_FORMAT="dd//MM//yyyy";
    //Member variables to handle Item clicks
    final private ItemClickListener mItemClickListener;
    private List<TaskEntry> mTaskEntries;
    private final Context mContext;
    //Date formatter
    private final SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext=context;
        mItemClickListener=listener;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the task_layout to a view
        View view= LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout,parent,false);
                return new TaskViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getBindingAdapterPosition()} which
     * will have the updated adapter position.
     * <p>
     * Override  instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //Determine the values of the wanted data
        TaskEntry taskEntry=mTaskEntries.get(position);
        String description=taskEntry.getDescription();
        int priority=taskEntry.getPriority();
        String updatedAt=dateFormat.format(taskEntry.getUpdatedAt());

        //Set the values
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);

        // Programmatically set the text and color for the priority TextView
        String priorityString=""+ priority; //converts into the String type
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle=(GradientDrawable) holder.priorityView.getBackground();
        //Get the appropriate background color based on the priority
        int priorityColor=getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    }
    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(mTaskEntries==null)
            return 0;
        return mTaskEntries.size();
    }
    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskEntry> taskEntries)
    {
        mTaskEntries=taskEntries;
        notifyDataSetChanged();
    }

    public List<TaskEntry> getTasks() {
        return mTaskEntries;
    }

    public interface ItemClickListener
    {
        void onItemClickListener(int itemId);
    }
    //Inner class for creating view holders
    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Class variables for the task description and priority TextViews
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;
        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDescriptionView=itemView.findViewById(R.id.taskDescription);
            updatedAtView=itemView.findViewById(R.id.taskUpdatedAt);
            priorityView=itemView.findViewById(R.id.priorityTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId=mTaskEntries.get(getBindingAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);

        }


    }
}
