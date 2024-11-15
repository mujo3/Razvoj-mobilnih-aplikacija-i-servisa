// Generated by view binder compiler. Do not edit!
package com.example.zadaca2_rmas.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.zadaca2_rmas.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class SeekbarsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView blueLabel;

  @NonNull
  public final SeekBar blueSeekBar;

  @NonNull
  public final TextView blueValueText;

  @NonNull
  public final TextView greenLabel;

  @NonNull
  public final SeekBar greenSeekBar;

  @NonNull
  public final TextView greenValueText;

  @NonNull
  public final TextView redLabel;

  @NonNull
  public final SeekBar redSeekBar;

  @NonNull
  public final TextView redValueText;

  private SeekbarsBinding(@NonNull LinearLayout rootView, @NonNull TextView blueLabel,
      @NonNull SeekBar blueSeekBar, @NonNull TextView blueValueText, @NonNull TextView greenLabel,
      @NonNull SeekBar greenSeekBar, @NonNull TextView greenValueText, @NonNull TextView redLabel,
      @NonNull SeekBar redSeekBar, @NonNull TextView redValueText) {
    this.rootView = rootView;
    this.blueLabel = blueLabel;
    this.blueSeekBar = blueSeekBar;
    this.blueValueText = blueValueText;
    this.greenLabel = greenLabel;
    this.greenSeekBar = greenSeekBar;
    this.greenValueText = greenValueText;
    this.redLabel = redLabel;
    this.redSeekBar = redSeekBar;
    this.redValueText = redValueText;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SeekbarsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SeekbarsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.seekbars, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SeekbarsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.blueLabel;
      TextView blueLabel = ViewBindings.findChildViewById(rootView, id);
      if (blueLabel == null) {
        break missingId;
      }

      id = R.id.blueSeekBar;
      SeekBar blueSeekBar = ViewBindings.findChildViewById(rootView, id);
      if (blueSeekBar == null) {
        break missingId;
      }

      id = R.id.blueValueText;
      TextView blueValueText = ViewBindings.findChildViewById(rootView, id);
      if (blueValueText == null) {
        break missingId;
      }

      id = R.id.greenLabel;
      TextView greenLabel = ViewBindings.findChildViewById(rootView, id);
      if (greenLabel == null) {
        break missingId;
      }

      id = R.id.greenSeekBar;
      SeekBar greenSeekBar = ViewBindings.findChildViewById(rootView, id);
      if (greenSeekBar == null) {
        break missingId;
      }

      id = R.id.greenValueText;
      TextView greenValueText = ViewBindings.findChildViewById(rootView, id);
      if (greenValueText == null) {
        break missingId;
      }

      id = R.id.redLabel;
      TextView redLabel = ViewBindings.findChildViewById(rootView, id);
      if (redLabel == null) {
        break missingId;
      }

      id = R.id.redSeekBar;
      SeekBar redSeekBar = ViewBindings.findChildViewById(rootView, id);
      if (redSeekBar == null) {
        break missingId;
      }

      id = R.id.redValueText;
      TextView redValueText = ViewBindings.findChildViewById(rootView, id);
      if (redValueText == null) {
        break missingId;
      }

      return new SeekbarsBinding((LinearLayout) rootView, blueLabel, blueSeekBar, blueValueText,
          greenLabel, greenSeekBar, greenValueText, redLabel, redSeekBar, redValueText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
