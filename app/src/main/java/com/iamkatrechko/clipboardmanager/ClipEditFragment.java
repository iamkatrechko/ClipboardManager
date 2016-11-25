package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.data.DatabaseDescription;

import static com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper.*;
import static com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class ClipEditFragment extends Fragment implements View.OnClickListener ,LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ClipEditFragment";
    private static final String ARG_URI = "param1";

    private static final int ONE_CLIP_LOADER = 0;
    private static final int ONE_CATEGORY_LOADER = 1;

    private static final int DIALOG_CANCEL_CHANGES = 465444;

    private boolean addingNewClip = false;
    private boolean isEditMode = false;
    private boolean saveNeed = false;

    private Uri clipUri;
    private EditText etTitle;
    private EditText etContent;
    private TextView tvDate;
    private TextView tvCategoryName;
    private ImageView ivIsFavorite;
    private FloatingActionButton fab;

    private long currentCategoryId = -1;
    private boolean isFavorite = false;

    private ImageView ivCopy;
    private ImageView ivShare;
    private LinearLayout linearCategory;
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            saveNeed = true;
            // текст только что изменили
            //Log.d(TAG, s + " - " + start + " - " + before + " - " + count);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // текст будет изменен
        }

        @Override
        public void afterTextChanged(Editable s) {
            // текст уже изменили
        }
    };

    public static ClipEditFragment newInstance(Uri uri) {
        ClipEditFragment fragment = new ClipEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            clipUri = getArguments().getParcelable(ARG_URI);
            if (clipUri == null) {
                addingNewClip = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clip_edit, container, false);

        Log.d(TAG, String.valueOf(clipUri));
        etTitle = (EditText) v.findViewById(R.id.etTitle);
        etContent = (EditText) v.findViewById(R.id.etContent);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvCategoryName = (TextView) v.findViewById(R.id.tvCategory);
        ivIsFavorite = (ImageView) v.findViewById(R.id.ivIsFavorite);
        ivCopy = (ImageView) v.findViewById(R.id.ivCopy);
        ivShare = (ImageView) v.findViewById(R.id.ivShare);
        linearCategory = (LinearLayout) v.findViewById(R.id.linearCategory);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        linearCategory.setOnClickListener(this);
        ivIsFavorite.setOnClickListener(this);
        ivCopy.setOnClickListener(this);
        ivShare.setOnClickListener(this);

        if (addingNewClip) {
            isEditMode = true;
            currentCategoryId = 0;
            etTitle.setText("Новое название");
            etContent.requestFocus();
        } else {
            getLoaderManager().initLoader(ONE_CLIP_LOADER, null, this);
        }

        isEditMode = true;
        if (isEditMode) {
            etTitle.setEnabled(true);
            etContent.setEnabled(true);
            toEditMode();
        }
        if (savedInstanceState != null) {
            currentCategoryId = savedInstanceState.getLong("currentCategoryId");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Если стоит режим просмотра
                if (!isEditMode) {
                    toEditMode();
                    etContent.requestFocus();
                    return;
                }else{
                    saveClip();
                }
            }
        });
        return v;
    }

    public void toEditMode() {
        isEditMode = true;
        etTitle.setEnabled(true);
        etContent.setEnabled(true);
        fab.setImageResource(R.drawable.ic_done_24dp);
    }

    public void saveClip() {
        // Создание объекта ContentValues с парами "ключ—значение"
        ContentValues contentValues = new ContentValues();
        contentValues.put(Clip.COLUMN_TITLE, etTitle.getText().toString());
        contentValues.put(Clip.COLUMN_CONTENT, etContent.getText().toString());
        contentValues.put(Clip.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(Clip.COLUMN_CATEGORY_ID, currentCategoryId);

        if (addingNewClip) {
            //FIXME Добавить дату создания записи
            contentValues.put(Clip.COLUMN_DATE, "newDate");
            contentValues.put(Clip.COLUMN_IS_DELETED, "0");

            Uri newClipUri = getActivity().getContentResolver().insert(Clip.CONTENT_URI, contentValues);

            if (newClipUri != null) {
                /*listener.onAddEditCompleted(newClipUri);*/
                Toast.makeText(getActivity(), "Создано", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Ошибка создания", Toast.LENGTH_SHORT).show();
            }
        } else {
            int updatedRows = getActivity().getContentResolver().update(
                    clipUri, contentValues, null, null);

            if (updatedRows > 0) {
                /*listener.onAddEditCompleted(newClipUri);*/
                Toast.makeText(getActivity(), "Сохранено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        }
        getActivity().finish();
    }

    private void deleteClip() {
        if (!addingNewClip) {
            getActivity().getContentResolver().delete(clipUri, null, null);
        }
        getActivity().finish();
    }

    private void cancel() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogSaveClip fragmentDialog = DialogSaveClip.newInstance();
        fragmentDialog.setTargetFragment(this, DIALOG_CANCEL_CHANGES);
        fragmentDialog.show(fragmentManager, "DIALOG_CANCEL_CHANGES");
    }

    private void copyToClipboard() {
        Util.copyToClipboard(getActivity(), etContent.getText().toString());
    }

    private void share() {
        Util.shareText(getActivity(), etTitle.getText().toString());
    }

    private void setIsFavorite(boolean favorite) {
        saveNeed = true;
        isFavorite = favorite;
        if (isFavorite) {
            ivIsFavorite.setImageResource(R.drawable.ic_star);
        } else {
            ivIsFavorite.setImageResource(R.drawable.ic_star_border);
        }
        /*ContentValues contentValues = new ContentValues();
        contentValues.put(Clip.COLUMN_IS_FAVORITE, isFavorite);

        getActivity().getContentResolver().update(clipUri, contentValues, null, null);*/
        //FIXME Сбрасывает все поля в режиме редактирования (что-то сделать с режимом)
        /*if (!isFavorite){
            ivIsFavorite.setImageResource(R.drawable.ic_star_border);
        }else{
            ivIsFavorite.setImageResource(R.drawable.ic_star);
        }*/
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        switch (vId) {
            case R.id.ivShare:
                share();
                break;
            case R.id.linearCategory:
                DialogManager.showDialogChangeCategory(this);
                break;
            case R.id.ivIsFavorite:
                setIsFavorite(!isFavorite);
                break;
            case R.id.ivCopy:
                copyToClipboard();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        switch (id) {
            case ONE_CLIP_LOADER:
                return new CursorLoader(getActivity(),
                        clipUri, // Uri отображаемого контакта
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
            case ONE_CATEGORY_LOADER:
                Uri categoryUri = DatabaseDescription.Category.buildClipUri(args.getLong("categoryId"));
                return new CursorLoader(getActivity(),
                        categoryUri, // Uri отображаемого контакта
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case ONE_CLIP_LOADER:
                    ClipboardCursor clipsData = new ClipboardCursor(data);

                    etTitle.setText(clipsData.getTitle());
                    etContent.setText(clipsData.getContent());
                    tvDate.setText(Util.getTimeInString(clipsData.getDate()));
                    if (currentCategoryId == -1) {
                        currentCategoryId = clipsData.getCategoryId();
                    }
                    isFavorite = clipsData.isFavorite();
                    if (isFavorite) {
                        ivIsFavorite.setImageResource(R.drawable.ic_star);
                    } else {
                        ivIsFavorite.setImageResource(R.drawable.ic_star_border);
                    }

                    etTitle.addTextChangedListener(mTextWatcher);
                    etContent.addTextChangedListener(mTextWatcher);

                    Bundle args = new Bundle();
                    args.putLong("categoryId", clipsData.getCategoryId());
                    getLoaderManager().restartLoader(ONE_CATEGORY_LOADER, args, this);
                    break;
                case ONE_CATEGORY_LOADER:
                    CategoryCursor categoryData = new CategoryCursor(data);
                    tvCategoryName.setText(categoryData.getTitle());
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_CHANGE_CATEGORY) {
            Toast.makeText(getActivity(), "Выбрана категория - " + data.getLongExtra("categoryId", 0), Toast.LENGTH_SHORT).show();
            currentCategoryId = data.getLongExtra("categoryId", 0);

            //Обновляем название категории в TextView (но не сохраняем)
            Bundle args = new Bundle();
            args.putLong("categoryId", data.getLongExtra("categoryId", 0));
            getLoaderManager().restartLoader(ONE_CATEGORY_LOADER, args, this);
            saveNeed = true;
            return;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DIALOG_CANCEL_CHANGES) {
            boolean save = data.getBooleanExtra("save", true);
            if (!save) {
                getActivity().finish();
            } else {
                saveClip();
            }
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("currentCategoryId", currentCategoryId);
        //FIXME Сохранить всю информацию при перевороте
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_clip, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                deleteClip();
            case android.R.id.home:
                backButtonWasPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void backButtonWasPressed() {
        if (isEditMode) {
            if (saveNeed) {
                cancel();
                //Toast.makeText(getActivity(), "Нужно сохранить", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().finish();
            }
        } else {
            getActivity().finish();
        }
    }
}
