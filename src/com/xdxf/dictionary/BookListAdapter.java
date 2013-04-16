package com.xdxf.dictionary;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.xdxf.dictionary.sqlite.Book;

import java.util.List;

public class BookListAdapter extends ArrayAdapter<Book> {
    private final Activity context;


    public BookListAdapter(Activity context, List<Book> list) {
        super(context, R.layout.item, list);
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // This is how you would determine if this particular item is checked
        // when the view gets created
        // --
        // final ListView lv = (ListView) parent;
        // final boolean isChecked = lv.isItemChecked(position);

        // The item we want to get the view for
        // --
        final Book item = getItem(position);

        // Re-use the view if possible
        // --
        View v = convertView;
        if (v == null) {
            v = context.getLayoutInflater().inflate(R.layout.item, null);
        }

        // Set some view properties (We should use the view holder pattern in
        // order to avoid all the findViewById and thus improve performance)
        // --
        final TextView captionView = (TextView) v
                .findViewById(R.id.itemId);
        if (captionView != null) {
            captionView.setText(item.getBookname());
        }
        final TextView idView = (TextView) v.findViewById(R.id.itemCaption);
        if (idView != null) {
            idView.setText(String.format("[%s-%s]",item.getFromLang(),item.getToLang())  );
        }



        return v;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_book_list1, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.bookName = (TextView) view.findViewById(R.id.bookmodel_name);
            viewHolder.bookFromTo = (TextView) view.findViewById(R.id.bookmodel_fromTo);
            viewHolder.bookDescription = (TextView) view.findViewById(R.id.bookmodel_desc);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            //viewHolder.image = (ImageView) view.findViewById(R.id.imageView);
            //viewHolder.checkbox.setOnCheckedChangeListener(l);
                    *//*.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            BookModel element = (BookModel) viewHolder.checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());

                        }
                    });*//*
            view.setTag(viewHolder);
            viewHolder.bookName.setTag(list.get(position));
        } else {
            view = convertView;
            //((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        Book b = list.get(position).getBook();
        holder.bookName.setText(b.getBookname());
        //holder.bookFromTo.setText(String.format("%s to %s", b.getFromLang(), b.getToLang()));
        //holder.bookDescription.setText(b.getDescription());
        //holder.image.setImageResource(b.isDirty() ? R.drawable.book_clock : R.drawable.book);
        holder.checkbox.setChecked(list.get(position).isSelected());
        return view;
    }

    static class ViewHolder {
        protected TextView bookName;
        protected TextView bookFromTo;
        protected TextView bookDescription;
        protected CheckBox checkbox;
        protected ImageView image;
    }*/
}
