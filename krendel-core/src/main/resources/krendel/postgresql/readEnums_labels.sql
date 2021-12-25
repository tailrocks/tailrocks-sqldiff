SELECT enumtypid, enumsortorder, enumlabel
FROM pg_enum
ORDER BY enumtypid, enumsortorder;