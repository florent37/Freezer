package fr.xebia.android.freezer.async;

/**
 * Created by florentchampigny on 19/02/2016.
 */
public abstract class SimpleCallback<T> implements Callback<T> {

    public abstract void onSuccess(T data) ;

    @Override
    public void onError(T data){}
}
