package com.example.a4;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A Fragment class that displays a photo along with its associated address.
 */
public class PhotoFragment extends Fragment {
    private final Bitmap photo;
    private final Address address;

    /**
     * Constructor for PhotoFragment.
     *
     * @param photo   The photo to display.
     * @param address The address associated with the photo.
     */
    public PhotoFragment(Bitmap photo, Address address) {
        this.photo = photo;
        this.address = address;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ImageView photoImageView = view.findViewById(R.id.photo_image_view);
        TextView addressTextView = view.findViewById(R.id.address_text_view);

        photoImageView.setImageBitmap(photo);
        addressTextView.setText(getFormattedAddress());

        return view;
    }

    /**
     * Formats the address into a displayable string.
     *
     * @return A formatted address string.
     */
    private String getFormattedAddress() {
        StringBuilder addressText = new StringBuilder();

        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressText.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex()) {
                addressText.append("\n");
            }
        }

        return addressText.toString();
    }
}