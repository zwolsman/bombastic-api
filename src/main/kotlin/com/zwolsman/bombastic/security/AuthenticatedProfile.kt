package com.zwolsman.bombastic.security

import com.zwolsman.bombastic.domain.Profile
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.util.Collections

class AuthenticatedProfile(val profile: Profile) : Authentication {
    override fun getName(): String {
        return profile.name
    }

    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        return Collections.emptyList()
    }

    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getDetails(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): Any {
        return profile
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isAuthenticated(): Boolean {
        return true
    }
}
