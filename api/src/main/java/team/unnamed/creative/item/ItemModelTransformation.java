/*
 * This file is part of creative, licensed under the MIT license
 *
 * Copyright (c) 2021-2025 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.creative.item;

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector3Float;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Represents the Minecraft 26.x item model {@code transformation} field.
 *
 * @sinceMinecraft 26.1
 * @sincePackFormat 84
 * @since 1.8.5
 */
public final class ItemModelTransformation implements Examinable {
    public static final float[] DEFAULT_ROTATION = {0F, 0F, 0F, 1F};

    private final float[] matrix;
    private final Vector3Float translation;
    private final Vector3Float scale;
    private final float[] leftRotation;
    private final float[] rightRotation;

    private ItemModelTransformation(
            final @Nullable float[] matrix,
            final @Nullable Vector3Float translation,
            final @Nullable Vector3Float scale,
            final @Nullable float[] leftRotation,
            final @Nullable float[] rightRotation
    ) {
        this.matrix = copyMatrix(matrix);
        this.translation = translation;
        this.scale = scale;
        this.leftRotation = copyRotation(leftRotation);
        this.rightRotation = copyRotation(rightRotation);
    }

    public static @NotNull ItemModelTransformation matrix(final float @NotNull [] matrix) {
        return new ItemModelTransformation(matrix, null, null, null, null);
    }

    public static @NotNull ItemModelTransformation decomposed(
            final @Nullable Vector3Float translation,
            final @Nullable Vector3Float scale,
            final float @Nullable [] leftRotation,
            final float @Nullable [] rightRotation
    ) {
        return new ItemModelTransformation(null, translation, scale, leftRotation, rightRotation);
    }

    public float @Nullable [] matrix() {
        return matrix == null ? null : matrix.clone();
    }

    public @Nullable Vector3Float translation() {
        return translation;
    }

    public @Nullable Vector3Float scale() {
        return scale;
    }

    public float @Nullable [] leftRotation() {
        return leftRotation == null ? null : leftRotation.clone();
    }

    public float @Nullable [] rightRotation() {
        return rightRotation == null ? null : rightRotation.clone();
    }

    public boolean isMatrix() {
        return matrix != null;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(
                ExaminableProperty.of("matrix", matrix),
                ExaminableProperty.of("translation", translation),
                ExaminableProperty.of("scale", scale),
                ExaminableProperty.of("leftRotation", leftRotation),
                ExaminableProperty.of("rightRotation", rightRotation)
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemModelTransformation)) return false;
        final ItemModelTransformation that = (ItemModelTransformation) o;
        return Arrays.equals(matrix, that.matrix)
                && Objects.equals(translation, that.translation)
                && Objects.equals(scale, that.scale)
                && Arrays.equals(leftRotation, that.leftRotation)
                && Arrays.equals(rightRotation, that.rightRotation);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(translation, scale);
        result = 31 * result + Arrays.hashCode(matrix);
        result = 31 * result + Arrays.hashCode(leftRotation);
        result = 31 * result + Arrays.hashCode(rightRotation);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return examine(StringExaminer.simpleEscaping());
    }

    private static float[] copyMatrix(final float @Nullable [] matrix) {
        if (matrix == null) {
            return null;
        }
        if (matrix.length != 16) {
            throw new IllegalArgumentException("Matrix transformation must contain 16 values");
        }
        return matrix.clone();
    }

    private static float[] copyRotation(final float @Nullable [] rotation) {
        if (rotation == null) {
            return null;
        }
        if (rotation.length != 4) {
            throw new IllegalArgumentException("Rotation quaternion must contain 4 values");
        }
        return requireNonNull(rotation, "rotation").clone();
    }
}
