package com.iamkatrechko.clipboardmanager.view.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor;
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor;
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils;
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils;
import com.iamkatrechko.clipboardmanager.view.DialogManager;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;

/**
 * Фрагмент экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipEditFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    /** Тег для логирования */
    private static final String TAG = ClipEditFragment.class.getSimpleName();

    /** Ключ аргумента. URI заметки */
    private static final String KEY_URI = "KEY_URI";

    /** Идентификатор загрузчика заметки */
    private static final int ONE_CLIP_LOADER = 0;
    /** Идентификатор загрузчика категории */
    private static final int ONE_CATEGORY_LOADER = 1;

    /** Создание новой заметки */
    private boolean addingNewClip;
    /** Режим редактирования */
    private boolean isEditMode;
    /** Необходимость сохранения */
    private boolean saveNeed;

    /** URI текущей заметки */
    private Uri clipUri;
    /** Текстовое поле с заголовком */
    private EditText etTitle;
    /** Текстовое поле с содержимым */
    private EditText etContent;
    /** Дата создания записи */
    private TextView tvDate;
    /** Название категории */
    private TextView tvCategoryName;
    /** Иконка принадлежности к избранным */
    private ImageView ivIsFavorite;
    /** Иконка сохранения/редкатирования */
    private FloatingActionButton fab;

    /** Идентификатор текущей категории */
    private long currentCategoryId = -1;
    /** Флаг принадлежности к избранным */
    private boolean isFavorite = false;

    /** Кнопка копирования в буфер */
    private ImageView ivCopy;
    /** Кнопка "поделиться" */
    private ImageView ivShare;
    /** Лэйаут с выбором категории */
    private LinearLayout linearCategory;
    /** Слушатель редактирования полей заголовка и содержимого */
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            saveNeed = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Возвращает новый экземпляр фрагмента
     * @param uri URI редактируемой заметки
     * @return новый экземпляр фрагмента
     */
    public static ClipEditFragment newInstance(@Nullable Uri uri) {
        ClipEditFragment fragment = new ClipEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            clipUri = getArguments().getParcelable(KEY_URI);
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
            currentCategoryId = 1;
            Bundle args = new Bundle();
            args.putLong("categoryId", currentCategoryId);
            getLoaderManager().restartLoader(ONE_CATEGORY_LOADER, args, this);
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
                } else {
                    saveClip();
                }
            }
        });
        return v;
    }

    /** Переключает в режим редактирования */
    public void toEditMode() {
        isEditMode = true;
        etTitle.setEnabled(true);
        etContent.setEnabled(true);
        fab.setImageResource(R.drawable.ic_done_24dp);
    }

    /** Сохраняет заметку */
    public void saveClip() {
        // Создание объекта ContentValues с парами "ключ—значение"
        if (etContent.getText().length() == 0) {
            Toast.makeText(getActivity(), "Введите текст записи!", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseDescription.ClipsTable.COLUMN_TITLE, etTitle.getText().toString());
        contentValues.put(DatabaseDescription.ClipsTable.COLUMN_CONTENT, etContent.getText().toString());
        contentValues.put(DatabaseDescription.ClipsTable.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID, currentCategoryId);

        if (addingNewClip) {
            //FIXME Добавить аттрибуты по умолчанию
            int titleLength = 25;
            if (etTitle.getText().length() == 0) {
                if (etContent.getText().length() < titleLength) {
                    titleLength = etContent.getText().length();
                }
                contentValues.put(DatabaseDescription.ClipsTable.COLUMN_TITLE, etContent.getText().toString().substring(0, titleLength));
            }
            contentValues.put(DatabaseDescription.ClipsTable.COLUMN_DATE, System.currentTimeMillis());
            contentValues.put(DatabaseDescription.ClipsTable.COLUMN_IS_DELETED, "0");

            Uri newClipUri = getActivity().getContentResolver().insert(DatabaseDescription.ClipsTable.CONTENT_URI, contentValues);

            if (newClipUri != null) {
                /*listener.onAddEditCompleted(newClipUri);*/
                Toast.makeText(getActivity(), "Добавлено", Toast.LENGTH_SHORT).show();
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

    /** Удаляет текущую заметку */
    private void deleteClip() {
        if (!addingNewClip) {
            getActivity().getContentResolver().delete(clipUri, null, null);
        }
        getActivity().finish();
    }

    /** Копирует заметку в буфер обмена */
    private void copyToClipboard() {
        ClipUtils.copyToClipboard(getContext(), etContent.getText().toString());
    }

    /** Расшаривает текущую заметку */
    private void share() {
        IntentUtils.INSTANCE.sendMail(getActivity(), etTitle.getText().toString());
    }

    /**
     * Изменяет принадлежность заметки к избранным
     * @param isFavorite принадлежность заметки к избранным
     */
    private void setIsFavorite(boolean isFavorite) {
        saveNeed = true;
        this.isFavorite = isFavorite;
        if (this.isFavorite) {
            ivIsFavorite.setImageResource(R.drawable.ic_star);
        } else {
            ivIsFavorite.setImageResource(R.drawable.ic_star_border);
        }
        /*ContentValues contentValues = new ContentValues();
        contentValues.put(ClipsTable.COLUMN_IS_FAVORITE, isFavorite);

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
                Uri categoryUri = DatabaseDescription.CategoryTable.buildClipUri(args.getLong("categoryId"));
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
                    ClipCursor clipsData = new ClipCursor(data);

                    etTitle.setText(clipsData.getTitle());
                    etContent.setText(clipsData.getContent());
                    tvDate.setText(DateFormatUtils.INSTANCE.getTimeInString(clipsData.getDate()));
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
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_CANCEL_CHANGES) {
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

    /** Нажатие на кнопку назад */
    public void backButtonWasPressed() {
        if (isEditMode) {
            if (saveNeed) {
                if (UtilPreferences.getShowSaveDialogBeforeExit(getActivity())) {
                    DialogManager.showDialogCancel(this);
                } else {
                    saveClip();
                    getActivity().finish();
                }
                //Toast.makeText(getActivity(), "Нужно сохранить", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().finish();
            }
        } else {
            getActivity().finish();
        }
    }
}
