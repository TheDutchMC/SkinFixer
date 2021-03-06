//! JNI bindings for dev.array21.skinfixer.storage.LibSkinFixer#init()

use crate::{jstring_to_string, optional_string};
use crate::config::{StorageType, Config};

use jni::objects::{JClass, JString};
use mysql::prelude::Queryable;
use std::path::PathBuf;
use std::str::FromStr;
use mysql::Params;
use jni::JNIEnv;

/// Java JNI function
///
/// dev.array21.skinfixer.storage.LibSkinFixer#init(String storageType, String host, String database, String username, String password, String storagePath)
#[no_mangle]
pub extern "system" fn Java_dev_array21_skinfixer_storage_LibSkinFixer_init(env: JNIEnv<'_>, _class: JClass<'_>, storage_type: JString<'_>, host: JString<'_>, database: JString<'_>, username: JString<'_>, password: JString<'_>, storage_path: JString<'_>) {
    let storage_type_str: String = jstring_to_string!(env, storage_type);
    let storage_type = match StorageType::from_str(&storage_type_str) {
        Ok(st) => st,
        Err(_) => panic!("Failed to convert String to StorageType, '{}' is not valid", &storage_type_str)
    };

    let host = jstring_to_string!(env, host);
    let database = jstring_to_string!(env, database);
    let username = jstring_to_string!(env, username);
    let password = jstring_to_string!(env, password);
    let storage_path = jstring_to_string!(env, storage_path);

    let config = Config::new(storage_type, optional_string!(host), optional_string!(database), optional_string!(username), optional_string!(password), optional_string!(storage_path));

    match config.get_type() {
        StorageType::Mysql => {
            let mut conn = match config.mysql_conn() {
                Ok(c) => c,
                Err(e) => panic!("{:?}", e)
            };

            match conn.exec::<usize, &str, Params>("CREATE TABLE IF NOT EXISTS skins (uuid VARCHAR(36) PRIMARY KEY, value TEXT, signature TEXT)", Params::Empty) {
                Ok(_) => {},
                Err(e) => panic!("Failed to create table 'skins': {:?}", e)
            }
        },
        StorageType::Postgres => {
            let mut conn = match config.postgres_conn() {
                Ok(c) => c,
                Err(e) => panic!("{:?}", e)
            };

            match conn.execute("CREATE TABLE IF NOT EXISTS skins (uuid VARCHAR(36) PRIMARY KEY, value TEXT, signature TEXT)", &[]) {
                Ok(_) => {},
                Err(e) => panic!("Failed to create table 'skins': {:?}", e)
            }
        },
        StorageType::Bin => {
            let mut path = PathBuf::from(config.get_path().unwrap());
            path.push("skins.bin");

            if !path.exists() {
                match std::fs::File::create(&path) {
                    Ok(_) => {},
                    Err(e) => panic!("Failed to create skins.bin file: {:?}", e)
                }
            }
        },
        StorageType::Sqlite => {
            let conn = match config.sqlite_conn() {
                Ok(c) => c,
                Err(e) => panic!("Failed to create SQLite connection: {:?}", e)
            };

            match conn.execute("CREATE TABLE IF NOT EXISTS skins (uuid TEXT PRIMARY KEY, value TEXT, signature TEXT)", rusqlite::params!{}) {
                Ok(_) => {},
                Err(e) => panic!("Failed to create table 'skins': {:?}", e)
            }
        }
    }

    let lock = crate::config::CONFIG.lock().expect("Failed to lock CONFIG Mutex");
    lock.replace(Some(config));
}