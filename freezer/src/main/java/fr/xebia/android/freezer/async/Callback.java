package fr.xebia.android.freezer.async;

/**
 * Created by florentchampigny on 19/02/2016.
 */
public interface Callback<T> {
    void onSuccess();
    void onError(T data);
}
