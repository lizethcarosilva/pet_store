-- PostgreSQL: rellenar pet_owner.client_id a partir de user_id cuando falta.
-- Ejecutar manualmente si no usas el arranque automático (PetOwnerClientIdBackfill).

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
WHERE po.pet_id = m.pet_id AND po.user_id = m.user_id AND po.client_id IS NULL;

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
WHERE po.pet_id = m.pet_id AND po.user_id = m.user_id AND po.client_id IS NULL;
