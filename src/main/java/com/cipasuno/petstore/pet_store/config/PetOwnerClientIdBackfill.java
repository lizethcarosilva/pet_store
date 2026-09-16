package com.cipasuno.petstore.pet_store.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Rellena pet_owner.client_id cuando solo existe user_id (datos legacy),
 * emparejando usuario con cliente por correo o identificación en el mismo tenant.
 */
@Component
@Order(2000)
public class PetOwnerClientIdBackfill implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public PetOwnerClientIdBackfill(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            int byCorreo = jdbcTemplate.update("""
                UPDATE pet_owner po
                SET client_id = m.client_id
                FROM (
                    SELECT po_inner.pet_id, po_inner.user_id, c.client_id
                    FROM pet_owner po_inner
                    INNER JOIN "user" u ON u.user_id = po_inner.user_id
                    INNER JOIN client c ON LOWER(TRIM(c.correo)) = LOWER(TRIM(u.correo))
                        AND c.tenant_id = u.tenant_id
                    WHERE po_inner.client_id IS NULL AND po_inner.user_id IS NOT NULL
                ) m
                WHERE po.pet_id = m.pet_id AND po.user_id = m.user_id AND po.client_id IS NULL
                """);
            int byIdent = jdbcTemplate.update("""
                UPDATE pet_owner po
                SET client_id = m.client_id
                FROM (
                    SELECT po_inner.pet_id, po_inner.user_id, c.client_id
                    FROM pet_owner po_inner
                    INNER JOIN "user" u ON u.user_id = po_inner.user_id
                    INNER JOIN client c ON TRIM(c.ident) = TRIM(u.ident)
                        AND c.tenant_id = u.tenant_id
                    WHERE po_inner.client_id IS NULL AND po_inner.user_id IS NOT NULL
                ) m
                WHERE po.pet_id = m.pet_id AND po.user_id = m.user_id AND po.client_id IS NULL
                """);
            if (byCorreo > 0 || byIdent > 0) {
                System.out.println("✅ pet_owner: client_id sincronizado desde tabla user (correo="
                    + byCorreo + ", ident=" + byIdent + ")");
            }
        } catch (Exception e) {
            System.err.println("⚠️ PetOwnerClientIdBackfill omitido (¿BD no PostgreSQL o tablas distintas?): "
                + e.getMessage());
        }
    }
}
