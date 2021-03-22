/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irn.vpn.newAct;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserViewModel extends ViewModel {

    /**
     * LiveData of FirebaseUser. This keeps the UI up-to-date.
     */
    public MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();

    /**
     * When the user changes, the app needs to notify the server.
     * A normal LiveData is called more often in order to keep the UI up-to-date.
     * This event is a more efficient indicator that the user might have changed.
     * This event could be fired more often than needed because the ViewModel
     * can be destroyed and recreated even if the user does not change.
     * To improve this, we could store authentication information to disk in order
     * to make sure we only call the server when the user actually changes,
     * however this implementation seems to be efficient enough for this sample.
     */
    public SingleLiveEvent<Void> userChangeEvent = new SingleLiveEvent<>();

    public FirebaseUserViewModel() {
        updateFirebaseUser();
    }

    /**
     * This method should be called when the user completes sign-in or sign-out.
     */
    public void updateFirebaseUser() {
        FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();

        if (newUser != null && firebaseUser.getValue() != null &&
                !newUser.getUid().equals(firebaseUser.getValue().getUid())) {
            userChangeEvent.call();
        }
        firebaseUser.postValue(newUser);
    }

    /**
     * Returns true if the user is currently signed in.
     */
    public boolean isSignedIn() {
        return firebaseUser.getValue() != null;
    }
}
