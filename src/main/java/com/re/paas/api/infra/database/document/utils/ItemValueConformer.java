package com.re.paas.api.infra.database.document.utils;

import static com.re.paas.api.utils.BinaryUtils.copyBytesFrom;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to standardize a given <code>Item</code>'s value into a standard
 * internal representation (for purposes such as equality comparison.)
 */
public class ItemValueConformer extends ValueTransformer {
    /**
     * This method is assumed to be called for the purpose of a setter method
     * invocation, but NOT a getter method invocation.
     */
    @Override public Object transform(Object value) {
        if (value == null) {
            return value;
        } else if (value instanceof Boolean) {
            return value;
        } else if (value instanceof String) {
            return value;
        } else if (value instanceof Number) {
            return ItemUtils.toBigDecimal((Number)value);
        } else if (value instanceof byte[]) {
            return value;
        } else if (value instanceof ByteBuffer) {
            return copyBytesFrom((ByteBuffer)value);
        } else if (value instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<Object> set = (Set<Object>) value;
            if (set.size() == 0)
                return value;
            Object element = set.iterator().next();
            if (element instanceof String) {
                return value;
            } else if (element instanceof BigDecimal) {
                return value;
            } else if (element instanceof Number) {
                @SuppressWarnings("unchecked")
                Set<Number> in = (Set<Number>) value;
                Set<BigDecimal> out = new LinkedHashSet<BigDecimal>(set.size());
                for (Number n : in)
                    out.add(ItemUtils.toBigDecimal(n));
                return out;
            } else if (element instanceof byte[]) {
                return value;
            } else if (element instanceof ByteBuffer) {
                @SuppressWarnings("unchecked")
                Set<ByteBuffer> bs = (Set<ByteBuffer>) value;
                Set<byte[]> out = new LinkedHashSet<byte[]>(bs.size());
                for (ByteBuffer bb: bs)
                    out.add(copyBytesFrom(bb));
                return out;
            } else {
                throw new UnsupportedOperationException("element type: "
                        + element.getClass());
            }
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> in = (List<Object>) value;
            if (in.size() == 0)
                return in;
            List<Object> out = new ArrayList<Object>();
            for (Object v : in)
                out.add(transform(v));
            return out;
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> in = (Map<String, Object>) value;
            if (in.size() == 0)
                return in;
            Map<String, Object> out = new LinkedHashMap<String, Object>(in.size());
            for (Map.Entry<String, Object> e : in.entrySet())
                out.put(e.getKey(), transform(e.getValue()));
            return out;
        } else {
            throw new UnsupportedOperationException("value type: "
                    + value.getClass());
        }
    }
}