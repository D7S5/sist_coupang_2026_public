package com.goodpang.util;

import java.io.IOException;

import com.goodpang.dto.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginUtil {

	public static MemberDTO requireLogin(
			HttpServletRequest request,
			HttpServletResponse response)
			throws IOException {

		HttpSession session =
				request.getSession();

		MemberDTO loginMember =
				(MemberDTO) session.getAttribute(
						"loginMember"
				);

		if (loginMember != null) {
			return loginMember;
		}

		String method =
				request.getMethod();

		String contextPath =
				request.getContextPath();

		if ("GET".equalsIgnoreCase(method)) {

			String requestURI =
					request.getRequestURI();

			String queryString =
					request.getQueryString();

			String redirectUrl =
					requestURI;

			if (queryString != null
					&& !queryString.isBlank()) {

				redirectUrl +=
						"?" + queryString;
			}

			session.setAttribute(
					"redirectAfterLogin",
					redirectUrl
			);

		} else {

			String referer =
					request.getHeader(
							"Referer"
					);

			if (referer != null
					&& !referer.isBlank()) {

				int contextIndex =
						referer.indexOf(
								contextPath
						);

				if (contextIndex >= 0) {

					String redirectUrl =
							referer.substring(
									contextIndex
							);

					session.setAttribute(
							"redirectAfterLogin",
							redirectUrl
					);
				}
			}
		}

		response.sendRedirect(
				contextPath
				+ "/login"
		);

		return null;
	}

	private LoginUtil() {
	}
}