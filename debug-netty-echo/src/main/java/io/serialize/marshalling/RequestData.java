package io.serialize.marshalling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/1
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestData implements Serializable {

    private static final long serialVersionUID = 7359175860641122157L;

    private String id;

    private String name;

    private String requestMessage;

    private byte[] attachment;
}
