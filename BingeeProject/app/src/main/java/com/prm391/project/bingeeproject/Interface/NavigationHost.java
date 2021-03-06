package com.prm391.project.bingeeproject.Interface;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void navigateTo(Fragment fragment, Bundle bundle, boolean addToBackstack);
    void logout();
    void login();
}
