package com.api.sic.backend.domain;


import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLDelete;

@SQLDelete(sql = "UPDATE usuario SET deleted_at = CURRENT_TIMESTAMP where id=?")
@SQLRestriction("deleted_at is null")
public class Relatorio {
    
}
